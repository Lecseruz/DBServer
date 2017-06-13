package controller;

import models.status.StatusJDBCTemplate;
import models.user.User;
import models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping(value = "/api/user/{nickname}")
public class UserController {
    final UserJDBCTemplate userJDBCTemplate;
    final StatusJDBCTemplate statusJDBCTemplate;

    @Autowired
    public UserController(UserJDBCTemplate userJDBCTemplate, StatusJDBCTemplate statusJDBCTemplate) {
        this.userJDBCTemplate = userJDBCTemplate;
        this.statusJDBCTemplate = statusJDBCTemplate;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@PathVariable(value = "nickname") String nickname, @RequestBody User user) throws IOException {
        user.setNickname(nickname);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            userJDBCTemplate.create(nickname, user.getFullname(), user.getAbout(), user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(userJDBCTemplate.getUsersByNicknameOrEmail(nickname, user.getEmail()));
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable(value = "nickname") String nickname) throws IOException {
        final User user = userJDBCTemplate.getUserByNickname(nickname);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@PathVariable(value = "nickname") String nickname, @RequestBody User user) throws IOException {
        user.setNickname(nickname);
        try {
            user = userJDBCTemplate.update(user.getAbout(), user.getEmail(), user.getFullname(), nickname);
        }catch (DuplicateKeyException d){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(userJDBCTemplate.getUserByNickname(nickname));
    }
}