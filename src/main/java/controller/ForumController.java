package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

import models.thread.Thread;
import models.forum.Forum;
import models.forum.ForumJDBCTemplate;
import models.status.StatusJDBCTemplate;
import models.thread.ThreadJDBCTemplate;
import models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/forum")
public class ForumController {
    private int countForum = 0;
    private int countThread = 0;
    final private ForumJDBCTemplate forumJDBCTemplate;
    final private StatusJDBCTemplate statusJDBCTemplate;
    final private UserJDBCTemplate userJDBCTemplate;
    final private ThreadJDBCTemplate threadJDBCTemplate;

    @Autowired
    public ForumController(ForumJDBCTemplate forumJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate, StatusJDBCTemplate statusJDBCTemplate, UserJDBCTemplate userJDBCTemplate) {
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.statusJDBCTemplate = statusJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
        countForum = forumJDBCTemplate.getCount();
        countThread = threadJDBCTemplate.getCount();
    }

    @RequestMapping(value = "/forum/create", method = RequestMethod.POST)
    public ResponseEntity<?> signOut(@RequestBody Forum forum) throws IOException {

        try {
            userJDBCTemplate.getUserByNickname(forum.getNickname());
        }catch(EmptyResultDataAccessException e){
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        try{
            forumJDBCTemplate.getForum(forum.getNickname(), forum.getTitle());
            return new ResponseEntity<Forum>(forumJDBCTemplate.getForum(forum.getNickname(), forum.getTitle()), HttpStatus.CONFLICT);
        } catch(EmptyResultDataAccessException e) {
            ++countForum;
            statusJDBCTemplate.updateCountForum(countForum);
            forumJDBCTemplate.create(forum.getTitle(), forum.getNickname(), forum.getSlag(), forum.getPosts(), forum.getThread());
            return new ResponseEntity<Forum>(forum, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/forum/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getWithSlug(@PathVariable(value = "slug") String slug) throws IOException {
        try{
            Forum forum = forumJDBCTemplate.getForumBySlug(slug);
            return new ResponseEntity<Forum>(forum, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e){
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/forum/{slug}/create", method = RequestMethod.GET)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug") String slug, @RequestBody Thread thread) throws IOException {
        Calendar calendar = Calendar.getInstance();
        Timestamp ourJavaTimestampObject = new Timestamp(calendar.getTime().getTime());
        thread.setCreated(ourJavaTimestampObject);
        try {
            userJDBCTemplate.getUserByNickname(thread.getAuthor());
            forumJDBCTemplate.getForum(thread.getAuthor(), thread.getForum());
        } catch (EmptyResultDataAccessException e){
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        try {
            threadJDBCTemplate.getThread(thread.getId());
            ++countThread;
            statusJDBCTemplate.updateCountThread(countThread);
            threadJDBCTemplate.create(thread.getId(), thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug(), thread.getCreated());
            return new ResponseEntity<Thread>(thread, HttpStatus.CREATED);
        } catch (DuplicateKeyException e){
            return new ResponseEntity<Thread>(thread, HttpStatus.CONFLICT);
        }
    }
}