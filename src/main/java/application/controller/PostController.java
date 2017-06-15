package application.controller;

import application.models.ResponseObject.ResponseInfoPost;
import application.models.forum.ForumJDBCTemplate;
import application.models.post.Post;
import application.models.post.PostJDBCTemplate;
import application.models.post.PostUpdate;
import application.models.thread.ThreadJDBCTemplate;
import application.models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
            if (post == null) {
                return ResponseEntity.notFound().build();// TODO : bad
            }
            if (!post.getMessage().equals(postUpdate.getMessage())) {
                final Post newPost = postJDBCTemplate.updatePost(postUpdate.getMessage(), post.getId());
                return ResponseEntity.ok(newPost);
            } else {
                return ResponseEntity.ok(post);
            }
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getInfoPost(@PathVariable(value = "id") int id, @RequestParam(name = "related", required = false) Set<String> related) {
        final ResponseInfoPost responseInfoPost = new ResponseInfoPost();
        responseInfoPost.setPost(postJDBCTemplate.getPostById(id));
        if (responseInfoPost.getPost() == null) {
            return ResponseEntity.notFound().build();// TODO : bad
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
    }
}
