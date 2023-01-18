package superapp.controllers;

import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.logic.concreteServices.MiniAppCommandService;
import superapp.logic.concreteServices.SuperAppObjectService;
import superapp.logic.concreteServices.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static superapp.util.Constants.DEFAULT_PAGE;
import static superapp.util.Constants.DEFAULT_PAGE_SIZE;

@RestController
public class AdminController {

    private UserService userService;
    private MiniAppCommandService miniappService;
    private SuperAppObjectService objectService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMiniAppCommandService(MiniAppCommandService MiniAppCommandService) {
        this.miniappService = MiniAppCommandService;
    }

    @Autowired
    public void setObjectService(SuperAppObjectService objectService) { this.objectService = objectService; }

    @RequestMapping(
            path = {"/superapp/admin/users"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary[] getAllUsers(
            @RequestParam(name = "userSuperapp", required = true,defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true,defaultValue = "") String email,
            @RequestParam(name = "size", required = false,defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false,defaultValue = DEFAULT_PAGE) int page) {
        return this.userService.getAllUsers(userSuperapp,email,size,page).toArray(new UserBoundary[0]);
    }

    @RequestMapping(
            path = {"/superapp/admin/miniapp"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MiniAppCommandBoundary[] exportMiniAppsCommands(
            @RequestParam(name = "userSuperapp", required = true,defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true,defaultValue = "") String email,
            @RequestParam(name = "size", required = false,defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false,defaultValue = DEFAULT_PAGE) int page) {
        return this.miniappService.getAllCommands(userSuperapp, email,size,page)
                .toArray(new MiniAppCommandBoundary[0]);
    }

    @RequestMapping(
            path= {"/superapp/admin/miniapp/{miniAppName}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MiniAppCommandBoundary[] exportSpecificMiniAppsCommands(
            @PathVariable("miniAppName") String miniAppName,
            @RequestParam(name = "userSuperapp", required = true,defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true,defaultValue = "") String email,
            @RequestParam(name = "size", required = false,defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false,defaultValue = DEFAULT_PAGE) int page) {
        return this.miniappService.getAllMiniAppCommands(miniAppName,userSuperapp, email,size,page)
                .toArray(new MiniAppCommandBoundary[0]);
    }

    @RequestMapping(
                path= {"/superapp/admin/users"},
                method = {RequestMethod.DELETE})
    public void deleteUsers(@RequestParam(name = "userSuperapp", required = true,defaultValue = "") String userSuperapp,
                            @RequestParam(name = "userEmail", required = true,defaultValue = "") String email)
                            { this.userService.deleteAllUsers(userSuperapp,email); }

    @RequestMapping(
            path= {"/superapp/admin/objects"},
            method = {RequestMethod.DELETE})
    public void deleteObjects(
            @RequestParam(name = "userSuperapp", required = true,defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true,defaultValue = "") String email)
            { this.objectService.deleteAllObjects(userSuperapp,email); }

    @RequestMapping(
            path= {"/superapp/admin/miniapp"},
            method = {RequestMethod.DELETE})
    public void deleteMiniApp(@RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
                              @RequestParam(name = "userEmail", required = true, defaultValue = "") String email)
                            { this.miniappService.deleteAllCommands(userSuperapp,email); }
}
