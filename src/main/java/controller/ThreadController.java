package controller;

import models.forum.Forum;
import models.forum.ForumJDBCTemplate;
import models.post.Post;
import models.post.PostJDBCTemplate;
import models.status.StatusJDBCTemplate;
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

/**
 * Created by magomed on 20.04.17.
 */
@RestController
@RequestMapping(value = "/api/thread")
public class ThreadController {
    private int id = 0;
    private final ForumJDBCTemplate forumJDBCTemplate;
    private final StatusJDBCTemplate statusJDBCTemplate;
    private final UserJDBCTemplate userJDBCTemplate;
    private final ThreadJDBCTemplate threadJDBCTemplate;
    private final PostJDBCTemplate postJDBCTemplate;

    @Autowired
    public ThreadController(ForumJDBCTemplate forumJDBCTemplate, PostJDBCTemplate postJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate, StatusJDBCTemplate statusJDBCTemplate, UserJDBCTemplate userJDBCTemplate) {
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.statusJDBCTemplate = statusJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
        this.postJDBCTemplate = postJDBCTemplate;
    }

    @RequestMapping(value = "/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug") String slug, @RequestBody List<Post> posts) throws IOException {
        try {
            int a;
            Thread thread = null;
            try {
                a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
            for (Post post : posts) {
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
                post.setCreated(thread.getCreated());
            }
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        postJDBCTemplate.createPosts(posts);
        for (Post post : posts) {
            post.setId(1);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(posts);
    }
}
