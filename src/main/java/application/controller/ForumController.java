package application.controller;

import application.models.Forum;
import application.models.Thread;
import application.service.api.*;
import application.service.exception.DuplicateResourceException;
import application.service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping(value = "/api/forum")
public class ForumController {
    private static Logger LOGGER = LoggerFactory.getLogger(ForumController.class);
    private final IForumService forumService;
    private final IUserService userService;
    private final IThreadService threadService;

    @Autowired
    public ForumController(IForumService forumService, IThreadService threadService, IUserService userService) {
        this.forumService = forumService;
        this.userService = userService;
        this.threadService = threadService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createForum(@RequestBody Forum forum) {
        try {
            forumService.createForum(forum);
            return ResponseEntity.status(HttpStatus.CREATED).body(forum);
        } catch (DuplicateResourceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumService.getForum(forum.getSlug()));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getWithSlug(@PathVariable(value = "slug") String slug) {
        try {
            return ResponseEntity.ok(forumService.getForum(slug));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug") String slug, @RequestBody Thread thread) {
        try {
            threadService.create(slug, thread);
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (DuplicateResourceException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadService.getThread(thread.getSlug()));
        }
    }

    @RequestMapping(value = "/{slug}/threads", method = RequestMethod.GET)
    public ResponseEntity<?> getThreads(@PathVariable(value = "slug") String slug, @RequestParam(value = "desc", defaultValue = "false") boolean desc, @RequestParam(value = "limit", defaultValue = "0") int limit, @RequestParam(value = "since", defaultValue = "") String created) throws IOException {
        try {
            return ResponseEntity.ok(threadService.getThreads(slug, desc, limit, created));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{slug}/users", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers(@PathVariable(value = "slug") String slug, @RequestParam(value = "desc", required = false, defaultValue = "false") boolean desc, @RequestParam(value = "limit", required = false, defaultValue = "0") int limit, @RequestParam(value = "since", required = false) String since) throws IOException {
        try {
            return ResponseEntity.ok(userService.getByForum(slug, limit, since, desc));
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}