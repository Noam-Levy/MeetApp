package superapp.logic.concreteServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.split.SplitDebtBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.converters.SuperAppObjectConverter;
import superapp.dal.SuperAppObjectEntityRepository;
import superapp.data.SuperAppObjectEntity;
import superapp.data.SuperappObjectPK;
import superapp.data.UserPK;
import superapp.logic.MiniAppServices;
import superapp.logic.SplitsService;
import superapp.util.exceptions.CannotProcessException;
import superapp.util.exceptions.InvalidInputException;
import superapp.util.exceptions.NotFoundException;
import superapp.util.wrappers.SuperAppObjectIdWrapper;

import java.util.*;
import java.util.stream.Collectors;

import static superapp.data.ObjectTypes.*;

@Service("Split")
public class SplitService implements SplitsService, MiniAppServices {
	private SuperAppObjectEntityRepository objectRepository;
	private SuperAppObjectConverter converter;
	private final String INVALID_AMOUNT_MESSAGE =  "Transaction must specify amount (number not less than or equal to 0)";

	@Autowired
	public SplitService(SuperAppObjectEntityRepository objectRepository) {
		this.objectRepository = objectRepository;
		this.converter = new SuperAppObjectConverter();
	}

	@Override
	public void handleObjectByType(SuperAppObjectBoundary object) {
		String objectType = object.getType();
		if (!isValidObjectType(objectType))
			objectType = "";
		switch (objectType) {
			case ("Group") -> this.checkGroupData(object);
			case ("Transaction") -> this.checkTransactionData(object);
			default -> throw new InvalidInputException("Unknown object type");
		}
	}

	@Override
	public Object runCommand(String miniapp, SuperAppObjectIdWrapper targetObject, UserIdBoundary invokedBy, String commandCase) {
		SuperAppObjectEntity group =
				this.objectRepository.findById(
						new SuperappObjectPK(
								targetObject.getObjectId().getSuperapp(),
								targetObject.getObjectId().getInternalObjectId()))
				.orElseThrow(() ->  new NotFoundException("Group not found"));

		if (!isUserInGroup(group, invokedBy))
			throw new InvalidInputException("Invoking user is not part of this group");

		if (!group.getActive())
			throw new InvalidInputException("Cannot execute commands on an inactive group");

		switch (commandCase) {
			case "showDebt" -> {
				SuperappObjectPK objectId =
						new SuperappObjectPK(group.getSuperapp(), group.getObjectId());
				return this.showDebt(objectId, invokedBy);
			}
			case "showAllDebts" -> { return this.showAllDebts(group); }
			case "settleGroupDebts" -> {
				this.settleGroupDebts(group);
				return null;
			}
			default -> throw new NotFoundException("Unknown command");
		}
	}

	@Override
	public void checkValidBinding(SuperAppObjectEntity parent, SuperAppObjectEntity child, UserPK userId) {
		if (!child.getType().equals(Transaction.name()))
			throw new CannotProcessException("Unknown bind request (Split)");

		if (!parent.getType().equals(Group.name()))
			throw new InvalidInputException("Transactions can only be bound to groups");

		if (!isUserInGroup(parent, new UserIdBoundary(userId.getSuperapp(), userId.getEmail())))
			throw new CannotProcessException("Transactions can only be bound by users in the group");
	}

	@Override
	public SplitDebtBoundary showDebt(SuperappObjectPK groupId, UserIdBoundary user) {
		Optional<SuperAppObjectEntity> groupOptional = this.objectRepository.findById(groupId);
		if (groupOptional.isEmpty())
			return null;

		SuperAppObjectEntity group = groupOptional.get();
		Set<SuperAppObjectEntity> transactions = group.getChildren();
		int totalMembers =
				((List<UserIdBoundary>)this.converter.detailsToMap(group.getObjectDetails())
				.get("members"))
				.size();
		if (totalMembers == 0) // guarding against division by zero
			throw new CannotProcessException("Group has no members");

		float totalPayments = 0, membersPayments = 0;
		for (SuperAppObjectEntity t : transactions) {
			if (!t.getActive()) // check if transaction has already been settled
				continue;

			float amount = doubleToFloat(this.converter.detailsToMap(t.getObjectDetails()).get("amount").toString());
			if (t.getCreatedBy().getUserId().equals(user))
				membersPayments += amount;
			totalPayments += amount;
		}

		float debt = membersPayments - (totalPayments / totalMembers);
		return new SplitDebtBoundary(user, debt > 0 ? 0 : debt);
	}

	@Override
	public Object showAllDebts(SuperAppObjectEntity group) {
		List<SplitDebtBoundary> allDebts = new ArrayList<>();
		((List<LinkedHashMap<String, String>>)this.converter
				.detailsToMap(group.getObjectDetails()).get("members"))
				.forEach(userData -> {
					UserIdBoundary userId = new UserIdBoundary(userData.get("superapp"), userData.get("email"));
					SuperappObjectPK objectId =
							new SuperappObjectPK(group.getSuperapp(), group.getObjectId());
					allDebts.add(showDebt(objectId, userId));
				});
		return allDebts.toArray(new SplitDebtBoundary[0]);
	}

	@Override
	public void settleGroupDebts(SuperAppObjectEntity group) {
		group.getChildren().forEach(t -> t.setActive(false));
	}

	private boolean isUserInGroup(SuperAppObjectEntity group, UserIdBoundary userId) {
		LinkedHashMap<String, String> linkedMap = new LinkedHashMap<>();
		linkedMap.put("superapp", userId.getSuperapp());
		linkedMap.put("email", userId.getEmail());

		return ((List<LinkedHashMap<String, String>>)this.converter
				.detailsToMap(group.getObjectDetails()).get("members"))
				.contains(linkedMap);
	}

	private void checkGroupData(SuperAppObjectBoundary group) {
		UserIdBoundary creatingUser = group.getCreatedBy().getUserId();
		Map<String, Object> objDetails = group.getObjectDetails();
		if (objDetails == null)
			throw new InvalidInputException("Group must specify it's members");
		List<UserIdBoundary> groupMembers = (List<UserIdBoundary>)objDetails.get("members");

		if (groupMembers == null || groupMembers.isEmpty() || groupMembers.size() < 2 ||
				!this.isUserInGroup(this.converter.toEntity(group), creatingUser))
			throw new InvalidInputException("Group must contain at least two members including its creator");

		SplitDebtBoundary[] debts = (SplitDebtBoundary[])this.showAllDebts(this.converter.toEntity(group));
		if (Arrays.stream(debts).anyMatch(member -> member != null && member.getDebt() < 0))
			throw new InvalidInputException("Cannot remove group members while any group member owes money");
	}

	private void checkTransactionData(SuperAppObjectBoundary transaction) {
		Map<String, Object> objDetails = transaction.getObjectDetails();
		if (objDetails == null)
			throw new InvalidInputException(INVALID_AMOUNT_MESSAGE);

		Object amountData = objDetails.get("amount");
		if (amountData == null)
			throw new InvalidInputException(INVALID_AMOUNT_MESSAGE);

		float amount = doubleToFloat(amountData.toString());
		if (amount <= 0) {
			transaction.setActive(false);
			throw new InvalidInputException(INVALID_AMOUNT_MESSAGE);
		}
	}

	private float doubleToFloat(String value) {
		try {
			return Double.valueOf(value).floatValue();
		} catch (Exception e) {
			throw new InvalidInputException(INVALID_AMOUNT_MESSAGE);
		}
	}
}
