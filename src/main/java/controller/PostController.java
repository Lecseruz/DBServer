package controller;

import com.sun.org.apache.regexp.internal.RE;
import models.post.Post;
import models.post.PostJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by magomed on 07.05.17.
 */

@RestController
@RequestMapping(value = "api/post")
public class PostController {
    private final PostJDBCTemplate postJDBCTemplate;

    public PostController(PostJDBCTemplate postJDBCTemplate) {
        this.postJDBCTemplate = postJDBCTemplate;
    }

    @RequestMapping(value = "{id}/details", method = RequestMethod.POST)
    public ResponseEntity<?> updatePost(@PathVariable(value = "{id}") Integer id, @RequestBody String postUpdate){
        try{
            Post post = postJDBCTemplate.getPostById(id);
            postJDBCTemplate.updatePost(postUpdate, post.getId());
            post.setMessage(postUpdate);
            return ResponseEntity.ok(post);
        } catch (EmptyResultDataAccessException e){
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
    }
}
