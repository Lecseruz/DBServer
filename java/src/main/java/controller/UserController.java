package controller;

import java.io.IOException;

import models.status.StatusJDBCTemplate;
import models.user.User;
import models.user.UserJDBCTemplate;
import org.postgresql.util.PSQLException;
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
            User user1 = userJDBCTemplate.getUserByNicknameAndEmail(nickname, user.getEmail());
            return new ResponseEntity<User>(user1, HttpStatus.CONFLICT);
        } catch (EmptyResultDataAccessException e) {
            userJDBCTemplate.create(nickname, user.getFullname(), user.getAbout(), user.getEmail());
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable(value = "nickname") String nickname) throws IOException {
        User user = null;
        try {
            user = userJDBCTemplate.getUserByNickname(nickname);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ResponseEntity<?> UpdateUser(@PathVariable(value = "nickname") String nickname, @RequestBody User user) throws IOException {
        user.setNickname(nickname);
        if (user.isEmpty()) {
            return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            User user1 = userJDBCTemplate.getUserByNickname(nickname);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }

        try{
            userJDBCTemplate.update(user.getNickname(), user.getAbout(), user.getFullname(), user.getEmail());
        } catch (DuplicateKeyException d){
            return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
}