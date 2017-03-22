package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Observable;

import application.Application;
import com.zetcode.conf.AppConfig;
import javafx.geometry.Pos;
import models.forum.Forum;
import models.post.Post;
import models.post.PostJDBCTemplate;
import models.status.StatusJDBCTemplate;
import models.thread.Thread;
import models.thread.ThreadJDBCTemplate;
import models.user.User;
import models.user.UserJDBCTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import models.forum.ForumJDBCTemplate;



@RestController
@RequestMapping(value = "")
public class UserAuthController {
    private int countUser = 0;
    private int countForum = 0;
    private int countPost = 0;
    private int countThread = 0;

    @RequestMapping(value = "/forum/create", method = RequestMethod.POST)
    public ResponseEntity<?> signOut(@RequestBody Forum forum) throws IOException {
        ++countForum;
        ForumJDBCTemplate forumJDBCTemplate = new ForumJDBCTemplate(AppConfig.primaryDataSource());
        UserJDBCTemplate userJDBCTemplate = new UserJDBCTemplate(AppConfig.primaryDataSource());
        StatusJDBCTemplate statusJDBCTemplate = new StatusJDBCTemplate(AppConfig.primaryDataSource());
        if (userJDBCTemplate.getUser(forum.getNickname()) != null) {
            if (forumJDBCTemplate.getForum(forum.getNickname(), forum.getTitle()) == null) {
                statusJDBCTemplate.updateCountForum(countForum);
                forumJDBCTemplate.create(forum.getTitle(), forum.getNickname(), forum.getSlag(), forum.getPosts(), forum.getThread());
                return new ResponseEntity<Forum>(forum, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<Forum>(forumJDBCTemplate.getForum(forum.getNickname(), forum.getTitle()), HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/forum/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getWithSlug(@PathVariable(value = "slug") String slug) throws IOException {
        ForumJDBCTemplate forumJDBCTemplate = new ForumJDBCTemplate(AppConfig.primaryDataSource());
        if (forumJDBCTemplate.getForumWithSlug(slug) != null){
            return new ResponseEntity<Forum>(forumJDBCTemplate.getForumWithSlug(slug), HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/forum/{slug}/create", method = RequestMethod.GET)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug") String slug, @RequestBody Thread thread) throws IOException {
        ++countThread;
        ForumJDBCTemplate forumJDBCTemplate = new ForumJDBCTemplate(AppConfig.primaryDataSource());
        UserJDBCTemplate userJDBCTemplate = new UserJDBCTemplate(AppConfig.primaryDataSource());
        StatusJDBCTemplate statusJDBCTemplate = new StatusJDBCTemplate(AppConfig.primaryDataSource());
        ThreadJDBCTemplate threadJDBCTemplate = new ThreadJDBCTemplate(AppConfig.primaryDataSource());
        Calendar calendar = Calendar.getInstance();
        Timestamp ourJavaTimestampObject = new Timestamp(calendar.getTime().getTime());
        thread.setCreated(ourJavaTimestampObject);
        if ((userJDBCTemplate.getUser(thread.getAuthor()) != null) && (forumJDBCTemplate.getForum(thread.getAuthor(), thread.getForum()) != null)){
            if(threadJDBCTemplate.getThread(thread.getId()) == null){
                statusJDBCTemplate.updateCountThread(countThread);
                threadJDBCTemplate.create(thread.getId(), thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug(), thread.getCreated());
                return new ResponseEntity<Thread>(thread, HttpStatus.CREATED);
            } else{
                return new ResponseEntity<Thread>(thread, HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/service/clear", method = RequestMethod.GET)
    public ResponseEntity<?> clear() throws IOException {
        countForum = 0;
        countPost = 0;
        countThread = 0;
        countUser = 0;
        ForumJDBCTemplate forumJDBCTemplate = new ForumJDBCTemplate(AppConfig.primaryDataSource());
        UserJDBCTemplate userJDBCTemplate = new UserJDBCTemplate(AppConfig.primaryDataSource());
        StatusJDBCTemplate statusJDBCTemplate = new StatusJDBCTemplate(AppConfig.primaryDataSource());
        ThreadJDBCTemplate threadJDBCTemplate = new ThreadJDBCTemplate(AppConfig.primaryDataSource());
        PostJDBCTemplate postJDBCTemplate = new PostJDBCTemplate(AppConfig.primaryDataSource());
        forumJDBCTemplate.delete();
        userJDBCTemplate.delete();
        statusJDBCTemplate.delete();
        threadJDBCTemplate.delete();
        postJDBCTemplate.delete();
        return new ResponseEntity<Object>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/thread/{slug_or_id}/create", method = RequestMethod.GET)
    public ResponseEntity<?> createPost(@PathVariable(value = "slug_or_id") String line, @RequestBody Post post) throws IOException {

        return new ResponseEntity<Object>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/forum/{nickname}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@PathVariable(value = "nickname") String nickname, @RequestBody User user) throws IOException {
        ++countUser;
        UserJDBCTemplate userJDBCTemplate = new UserJDBCTemplate(AppConfig.primaryDataSource());
        StatusJDBCTemplate statusJDBCTemplate = new StatusJDBCTemplate(AppConfig.primaryDataSource());
        if (userJDBCTemplate.getUserWithEmail(nickname, user.getEmail()) == null) {
            user.setNickname(nickname);
            userJDBCTemplate.create(nickname, user.getFullname(), user.getAbout(), user.getEmail());
            statusJDBCTemplate.updateCountUser(countUser);
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(userJDBCTemplate.getUserWithEmail(nickname,user.getEmail()), HttpStatus.CONFLICT);
        }
    }
}