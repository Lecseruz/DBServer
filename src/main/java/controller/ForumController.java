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

//    private static final Logger LOGGER = Logger.getLogger("ForumController");


    @Autowired
    public ForumController(ForumJDBCTemplate forumJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate, UserJDBCTemplate userJDBCTemplate) {
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> signOut(@RequestBody Forum forum) throws IOException {
        try {
            final User user = userJDBCTemplate.getUserByNickname(forum.getUser());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            forum.setUser(user.getNickname());
            forumJDBCTemplate.create(forum.getTitle(), forum.getUser(), forum.getSlug());
//            LOGGER.debug("forum created success");
            return ResponseEntity.status(HttpStatus.CREATED).body(forum);
        } catch (DuplicateKeyException e) {
//            LOGGER.debug(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumJDBCTemplate.getForumBySlug(forum.getSlug()));
        }
    }

    @RequestMapping(value = "/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getWithSlug(@PathVariable(value = "slug") String slug) throws IOException {
        try {
            final Forum forum = forumJDBCTemplate.getForumBySlug(slug);
            return ResponseEntity.ok(forum);
        } catch (EmptyResultDataAccessException e) {
//            LOGGER.debug(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug") String slug, @RequestBody Thread thread) throws IOException {
        try {
            if (userJDBCTemplate.getUserByNickname(thread.getAuthor()) == null){ // TODO: bad
                return ResponseEntity.notFound().build();
            }
            final Forum forum = forumJDBCTemplate.getForumBySlug(slug);
            thread.setForum(forum.getSlug());
            threadJDBCTemplate.create(thread);
//            LOGGER.debug("thread create success");
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);
        } catch (EmptyResultDataAccessException | NullPointerException e) {
//            LOGGER.debug(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (DuplicateKeyException e) {
//            LOGGER.debug(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadJDBCTemplate.getThreadBySlug(thread.getSlug()));
        }
    }

    @RequestMapping(value = "/{slug}/threads", method = RequestMethod.GET)
    public ResponseEntity<?> getThreads(@PathVariable(value = "slug") String slug, @RequestParam(value = "desc", defaultValue = "false") boolean desc, @RequestParam(value = "limit", defaultValue = "0") int limit, @RequestParam(value = "since", defaultValue = "") String created) throws IOException {
        try {
            final Forum forum = forumJDBCTemplate.getForumBySlug(slug);
            final List<Thread> threads = threadJDBCTemplate.getThreads(forum.getId(), desc, limit, created);
//        LOGGER.debug("get threads success");
            return ResponseEntity.ok(threads);
        } catch (EmptyResultDataAccessException e) {
//            LOGGER.debug(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{slug}/users", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers(@PathVariable(value = "slug") String slug, @RequestParam(value = "desc", required = false, defaultValue = "false") boolean desc, @RequestParam(value = "limit", required = false, defaultValue = "0") int limit, @RequestParam(value = "since", required = false) String since) throws IOException {
        try {
            final Forum forum = forumJDBCTemplate.getForumBySlug(slug);
            final List<User> users = userJDBCTemplate.getByForum(forum.getId(), limit, since, desc);
//        LOGGER.debug("get Forum success");
            return ResponseEntity.ok(users);
        } catch (EmptyResultDataAccessException e) {
//            LOGGER.debug(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}