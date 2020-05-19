package application.controller;

import application.models.Post;
import application.models.Thread;
import application.models.ThreadUpdate;
import application.models.Voice;
import application.service.api.*;
import application.service.exception.DuplicateResourceException;
import application.service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by magomed on 20.04.17.
 */
@RestController
@RequestMapping(value = "/api/thread")
public class ThreadController {
    private static Logger LOGGER = LoggerFactory.getLogger(ThreadController.class);

    private IThreadService threadService;
    private IPostService postService;
    private IVoiceService voiceService;


    @Autowired
    public ThreadController(IThreadService threadService, IPostService postService, IVoiceService voiceService) {
        this.threadService = threadService;
        this.postService = postService;
        this.voiceService = voiceService;
    }

    @RequestMapping(value = "/{slug_or_id}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug_or_id") String slugOrId, @RequestBody List<Post> posts) {
        postService.createPosts(slugOrId, posts);
        return ResponseEntity.status(HttpStatus.CREATED).body(posts);
    }


    @RequestMapping(value = "/{slug_or_id}/vote", method = RequestMethod.POST)
    public ResponseEntity<?> addVoice(@PathVariable(value = "slug_or_id") String slugOrId, @RequestBody Voice voice) {
        try {
            return ResponseEntity.ok(voiceService.addVoice(slugOrId, voice));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getThread(@PathVariable(value = "slug_or_id") String slugOrId) {
        try {
            final Thread thread = threadService.getThread(slugOrId);
            return ResponseEntity.ok(thread);
        } catch (DuplicateResourceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.POST)
    public ResponseEntity<?> updateThreads(@PathVariable(value = "slug_or_id") String
                                                   slugOrId, @RequestBody ThreadUpdate threadUpdate) {
        try {
            return ResponseEntity.ok(threadService.updateThreads(slugOrId, threadUpdate));
        } catch (DuplicateResourceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @RequestMapping(path = "/{slug_or_id}/posts", method = RequestMethod.GET)
    public ResponseEntity<?> postsThread(@PathVariable(name = "slug_or_id") String slug,
                                         @RequestParam(name = "limit", required = false) Integer limit,
                                         @RequestParam(name = "marker", required = false, defaultValue = "0") String marker,
                                         @RequestParam(name = "sort", required = false, defaultValue = "flat") String sort,
                                         @RequestParam(name = "desc", required = false, defaultValue = "false") Boolean desc) {
        try {
            return ResponseEntity.ok(threadService.getPostsOfThread(slug, limit, marker, sort, desc));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
