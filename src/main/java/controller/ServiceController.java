package controller;

import models.forum.ForumJDBCTemplate;
import models.post.PostJDBCTemplate;
import models.status.StatusJDBCTemplate;
import models.thread.ThreadJDBCTemplate;
import models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by magomed on 22.03.17.
 */

@RestController
@RequestMapping(value = "/api/service")
public class ServiceController {
    private final StatusJDBCTemplate statusJDBCTemplate;

    @Autowired
    public ServiceController(StatusJDBCTemplate statusJDBCTemplate) {
        this.statusJDBCTemplate = statusJDBCTemplate;
        statusJDBCTemplate.dropTable();
        statusJDBCTemplate.createTable();
    }

    @RequestMapping(value = "/service/clear", method = RequestMethod.GET)
    public ResponseEntity<?> clear() throws IOException {
        statusJDBCTemplate.dropTable();
        return new ResponseEntity<Object>(null, HttpStatus.OK);
    }
}
