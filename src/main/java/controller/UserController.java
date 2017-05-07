package controller;

import java.io.IOException;
import java.util.List;

import models.status.StatusJDBCTemplate;
import models.user.User;
import models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
            return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            userJDBCTemplate.create(nickname, user.getFullname(), user.getAbout(), user.getEmail());
            return new ResponseEntity<User>(user, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<List<User>>(userJDBCTemplate.getUserByNicknameAndEmail(nickname, user.getEmail()), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable(value = "nickname") String nickname) throws IOException {
        try {
            final User user = userJDBCTemplate.getUserByNickname(nickname);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@PathVariable(value = "nickname") String nickname, @RequestBody User user) throws IOException {
        user.setNickname(nickname);
        try {
            userJDBCTemplate.getUserByNickname(nickname);
            if (user.getFullname() != null){
                userJDBCTemplate.updateFullname(user.getFullname(), nickname);
            }
            if (user.getAbout() != null){
                userJDBCTemplate.updateAbbout(user.getAbout(), nickname);
            }
            if (user.getEmail() != null){
                userJDBCTemplate.updateEmail(user.getEmail(), nickname);
            }
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }

        catch (DuplicateKeyException d){
            return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<User>(userJDBCTemplate.getUserByNickname(nickname), HttpStatus.OK);
    }
}