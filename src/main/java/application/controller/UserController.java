package application.controller;

import application.models.User;
import application.service.api.DuplicateResourceException;
import application.service.api.IUserService;
import application.service.api.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/user/{nickname}")
public class UserController {
    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@PathVariable(value = "nickname") String nickname, @RequestBody User user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(nickname, user));
        } catch (DuplicateResourceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userService.getUsersByNicknameOrEmail(nickname, user.getEmail()));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable(value = "nickname") String nickname) {
        try {
            return ResponseEntity.ok(userService.getUser(nickname));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@PathVariable(value = "nickname") String nickname, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.update(nickname, user));
        } catch (DuplicateResourceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}