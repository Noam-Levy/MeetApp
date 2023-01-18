package superapp.controllers;

import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.logic.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsersController {

    private UsersService usersService;

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @RequestMapping(
            path= {"/superapp/users/login/{superapp}/{email}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary login (@PathVariable("superapp") String superapp, @PathVariable("email") String email) {
        return this.usersService.login(superapp, email);
    }

    @RequestMapping(
            path= {"/superapp/users"},
            method = {RequestMethod.POST},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary createUser (@RequestBody NewUserBoundary newUser) {
        return this.usersService.createUser(new UserBoundary(newUser.getEmail(), newUser.getRole(),
                newUser.getUsername(), newUser.getAvatar()));
    }

    @RequestMapping(
            path= {"/superapp/users/{superapp}/{userEmail}"},
            method = {RequestMethod.PUT},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void update (
            @PathVariable("superapp") String superapp,
            @PathVariable("userEmail") String email,
            @RequestBody UserBoundary update) {
        this.usersService.updateUser(superapp, email, update);
    }
}