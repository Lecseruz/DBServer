package application.controller;

import application.models.PostUpdate;
import application.service.api.IPostService;
import application.service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by magomed on 07.05.17.
 */

@RestController
@RequestMapping(value = "api/post")
public class PostController {
    private final IPostService postService;
    private static Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Autowired
    public PostController(IPostService postService) {
        this.postService = postService;
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.POST)
    public ResponseEntity<?> updatePost(@PathVariable(value = "id") int id, @RequestBody PostUpdate postUpdate) {
        try {
            return ResponseEntity.ok(postService.update(id, postUpdate));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getInfoPost(@PathVariable(value = "id") int id, @RequestParam(name = "related", required = false) Set<String> related) {
        try {
            return ResponseEntity.ok(postService.getInfoPost(id, related));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
