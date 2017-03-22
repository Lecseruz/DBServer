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
@RequestMapping(value = "/service")
public class ServiceController {
    private final StatusJDBCTemplate statusJDBCTemplate;
    private final ThreadJDBCTemplate threadJDBCTemplate;
    private final ForumJDBCTemplate forumJDBCTemplate;
    private final PostJDBCTemplate postJDBCTemplate;
    private final UserJDBCTemplate userJDBCTemplate;

    @Autowired
    public ServiceController(StatusJDBCTemplate statusJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate, ForumJDBCTemplate forumJDBCTemplate, PostJDBCTemplate postJDBCTemplate, UserJDBCTemplate userJDBCTemplate) {
        this.statusJDBCTemplate = statusJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
        this.postJDBCTemplate = postJDBCTemplate;
    }

    @RequestMapping(value = "/service/clear", method = RequestMethod.GET)
    public ResponseEntity<?> clear() throws IOException {
        forumJDBCTemplate.delete();
        userJDBCTemplate.delete();
        statusJDBCTemplate.delete();
        threadJDBCTemplate.delete();
        postJDBCTemplate.delete();
        return new ResponseEntity<Object>(null, HttpStatus.OK);
    }
}
