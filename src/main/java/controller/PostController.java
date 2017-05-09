package controller;

import models.ResponseObject.ResponseInfoPost;
import models.forum.ForumJDBCTemplate;
import models.post.Post;
import models.post.PostJDBCTemplate;
import models.post.PostUpdate;
import models.thread.ThreadJDBCTemplate;
import models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by magomed on 07.05.17.
 */

@RestController
@RequestMapping(value = "api/post")
public class PostController {
    private final PostJDBCTemplate postJDBCTemplate;
    private final UserJDBCTemplate userJDBCTemplate;
    private final ThreadJDBCTemplate threadJDBCTemplate;
    private final ForumJDBCTemplate forumJDBCTemplate;

    @Autowired
    public PostController(PostJDBCTemplate postJDBCTemplate, UserJDBCTemplate userJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate, ForumJDBCTemplate forumJDBCTemplate) {
        this.postJDBCTemplate = postJDBCTemplate;
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.POST)
    public ResponseEntity<?> updatePost(@PathVariable(value = "id") int id, @RequestBody PostUpdate postUpdate) {
        try {
            final Post post = postJDBCTemplate.getPostById(id);
            postJDBCTemplate.updatePost(postUpdate.getMessage(), post.getId());
            post.setMessage(postUpdate.getMessage());
            post.setIsEdited(true);
            return ResponseEntity.ok(post);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getInfoPost(@PathVariable(value = "id") int id, @RequestParam(name = "related", required = false) Set<String> related) {
        try {
            final ResponseInfoPost responseInfoPost = new ResponseInfoPost();
            responseInfoPost.setPost(postJDBCTemplate.getPostById(id));
            if (responseInfoPost.getPost() == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);// TODO : bad
            }
            if (related != null) {
                if (related.contains("user")) {
                    responseInfoPost.setAuthor(userJDBCTemplate.getUserByNickname(responseInfoPost.getPost().getAuthor()));
                }
                if (related.contains("forum")) {
                    responseInfoPost.setForum(forumJDBCTemplate.getForumBySlug(responseInfoPost.getPost().getForum()));
                }
                if (related.contains("thread")) {
                    responseInfoPost.setThread(threadJDBCTemplate.getThreadById(responseInfoPost.getPost().getThread()));
                }
            }
            return ResponseEntity.ok(responseInfoPost);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
