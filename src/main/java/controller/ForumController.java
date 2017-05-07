package controller;

import models.forum.Forum;
import models.forum.ForumJDBCTemplate;
import models.thread.Thread;
import models.thread.ThreadJDBCTemplate;
import models.user.User;
import models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(value = "/api/forum")
public class ForumController {
    private final ForumJDBCTemplate forumJDBCTemplate;
    private final UserJDBCTemplate userJDBCTemplate;
    private final ThreadJDBCTemplate threadJDBCTemplate;

    @Autowired
    public ForumController(ForumJDBCTemplate forumJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate, UserJDBCTemplate userJDBCTemplate) {
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> signOut(@RequestBody Forum forum) throws IOException {

        try {
            userJDBCTemplate.getUserByNickname(forum.getUser());
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        try {
            forumJDBCTemplate.create(forum.getTitle(), forum.getUser(), forum.getSlug(), forum.getPosts(), forum.getThread());
            final User user = userJDBCTemplate.getUserByNickname(forum.getUser());
            forum.setUser(user.getNickname());
            return new ResponseEntity<Forum>(forum, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumJDBCTemplate.getForumBySlug(forum.getSlug()));
        }
    }

    @RequestMapping(value = "/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getWithSlug(@PathVariable(value = "slug") String slug) throws IOException {
        try {
            final Forum forum = forumJDBCTemplate.getForumBySlug(slug);
            final User user = userJDBCTemplate.getUserByNickname(forum.getUser());
            forum.setUser(user.getNickname());
            return new ResponseEntity<Forum>(forum, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug") String slug, @RequestBody Thread thread) throws IOException {
        try {
            userJDBCTemplate.getUserByNickname(thread.getAuthor());
            final Forum forum = forumJDBCTemplate.getForumBySlug(slug);
            thread.setForum(forum.getSlug());
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        } catch (NullPointerException ignored) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        try {
            threadJDBCTemplate.create(thread);
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<Thread>(threadJDBCTemplate.getThreadBySlug(thread.getSlug()), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{slug}/threads", method = RequestMethod.GET)
    public ResponseEntity<?> getThreads(@PathVariable(value = "slug") String slug, @RequestParam(value = "desc", defaultValue = "false") boolean desc, @RequestParam(value = "limit", defaultValue = "0") int limit, @RequestParam(value = "since", defaultValue = "") String created) throws IOException {
        try {
            forumJDBCTemplate.getForumBySlug(slug);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        final List<Thread> threads = threadJDBCTemplate.getThreads(slug, desc, limit, created);
        return ResponseEntity.ok(threads);
    }

    @RequestMapping(value = "/{slug}/users", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers(@PathVariable(value = "slug") String slug, @RequestParam(value = "desc", required = false, defaultValue = "false") boolean desc, @RequestParam(value = "limit", required = false, defaultValue = "0") int limit, @RequestParam(value = "since", required = false, defaultValue = "") String since) throws IOException {
        try {
            forumJDBCTemplate.getForumBySlug(slug);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        final int a = Integer.parseInt(since);
        final List<User> users = forumJDBCTemplate.getUsers(slug, desc, limit, a);
        return ResponseEntity.ok(users);
    }
}