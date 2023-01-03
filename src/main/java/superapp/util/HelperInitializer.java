package superapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.MiniAppCommandIdBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.logic.concreteServices.MiniAppCommandService;
import superapp.logic.concreteServices.SuperAppObjectService;
import superapp.logic.concreteServices.UserService;
import superapp.util.wrappers.UserIdWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component // TODO fix to initialize object ONLY when testing manually the server
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
		//user

		List<UserBoundary> usersList =
				IntStream
						.range(0, 2)
						.mapToObj(i->
								this.users.createUser(new UserBoundary(
										new UserIdBoundary(
												users.getSuperappName(),
												"user"+i+"@test.com"),
										"SUPERAPP_USER",
										""+i,
										"a")))
						.toList();

		//object group

		List<SuperAppObjectBoundary> groupList =
				IntStream
						.range(0, 2)
						.mapToObj(i->
								this.objects.createObject(new SuperAppObjectBoundary(
										new SuperAppObjectIdBoundary(),
										"Group"+i,
										"a",
										new HashMap<String, Object>(){{put("members",usersList);}},
										new UserIdWrapper(usersList.get(0).getUserId()))))

						.toList();

//		//command

		List<Object> commandsList =
				IntStream
						.range(0, 2)
						.mapToObj(i->
								this.commands.invokeCommand(new MiniAppCommandBoundary(
										new MiniAppCommandIdBoundary(
												"Split",
												""+i
										),
										"showDebt",
										groupList.get(0).getObjectId(),
										usersList.get(0).getUserId(),
										new HashMap<String, Object>()
								)
								)).toList();
	}
}
