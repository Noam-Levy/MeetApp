package superapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.MiniAppCommandIdBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.logic.concreteServices.MiniAppCommandService;
import superapp.logic.concreteServices.SuperAppObjectService;
import superapp.logic.concreteServices.UserService;
import superapp.util.wrappers.UserIdWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Profile("staging")
public class HelperInitializer implements CommandLineRunner {
	private MiniAppCommandService commands;
	private SuperAppObjectService objects;
	private UserService users;

	@Autowired
	public HelperInitializer(MiniAppCommandService commands, SuperAppObjectService objects, UserService users) {
		super();
		this.commands = commands;
		this.objects = objects;
		this.users = users;
	}

	@Override
	public void run(String... args) throws Exception {
		// SUPERAPP_USER(s) creation
		List<UserBoundary> usersList =
				IntStream
				.range(0, 2)
				.mapToObj(i -> {
					UserBoundary user = new UserBoundary(users.getSuperappName(), "user" + i + "@test.com",
							"SUPERAPP_USER", "user" + i, "avatar" + i);
					return this.users.createUser(user);
				})
				.toList();
		// ADMIN creation
		UserBoundary admin = new UserBoundary(users.getSuperappName(), "admin@test.com",
				"ADMIN", "admin", "avatar");

		// Group object creation
		List<SuperAppObjectBoundary> groupList =
				IntStream
				.range(0, 2)
				.mapToObj(i-> this.objects.createObject(new SuperAppObjectBoundary(
						null,
						"Group"+i,
						"a",
						new HashMap<String, Object>(){{ put("members", usersList); }},
						new UserIdWrapper(usersList.get(0).getUserId()))))
				.toList();

		// Transaction object creation
		SuperAppObjectBoundary t1 =
				this.objects.createObject(new SuperAppObjectBoundary(
				null,
				"Transaction",
				"pizza",
				new HashMap<String, Object>(){{ put("amount", 75); }},
				new UserIdWrapper(usersList.get(0).getUserId())));
		SuperAppObjectBoundary t2 =
				this.objects.createObject(new SuperAppObjectBoundary(
				null,
				"Transaction",
				"snacks",
				new HashMap<String, Object>(){{ put("amount", 34.5); }},
				new UserIdWrapper(usersList.get(1).getUserId())));
		// bind created transactions to group
//		this.objects.bindNewChild(this.objects.getSuperappName(),
//				groupList.get(0).getObjectId().getInternalObjectId(), t1.getObjectId());
//		this.objects.bindNewChild(this.objects.getSuperappName(),
//				groupList.get(0).getObjectId().getInternalObjectId(), t2.getObjectId());

		// change user 1 to be miniapp user
		UserBoundary miniappUser = usersList.get(1);
		miniappUser.setRole("MINIAPP_USER");
		this.users.updateUser(miniappUser.getUserId().getSuperapp(), miniappUser.getUserId().getEmail(), miniappUser);

		// Commands creation
		List<Object> commandsList =
				IntStream
				.range(0, 2)
				.mapToObj(i-> {
					MiniAppCommandIdBoundary commandId = new MiniAppCommandIdBoundary("Split", ""+i);
					MiniAppCommandBoundary command = new MiniAppCommandBoundary(commandId, "showDebt",
							groupList.get(0).getObjectId(), usersList.get(1).getUserId(),
							new HashMap<String, Object>());
					return this.commands.invokeCommand(command);
				})
				.toList();
	}
}
