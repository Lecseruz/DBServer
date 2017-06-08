package controller;

import models.ResponseObject.ResponsePosts;
import models.post.Post;
import models.post.PostJDBCTemplate;
import models.thread.Thread;
import models.thread.ThreadJDBCTemplate;
import models.thread.ThreadUpdate;
import models.user.User;
import models.user.UserJDBCTemplate;
import models.voice.Voice;
import models.voice.VoiceJDBCTemplate;
import org.apache.log4j.Logger;
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
    private final ThreadJDBCTemplate threadJDBCTemplate;
    private final PostJDBCTemplate postJDBCTemplate;
    private final VoiceJDBCTemplate voiceJDBCTemplate;
    private final UserJDBCTemplate userJDBCTemplate;
    private static final Logger LOGGER = Logger.getLogger(ThreadController.class);


    @Autowired
    public ThreadController(VoiceJDBCTemplate voiceJDBCTemplate, UserJDBCTemplate userJDBCTemplate, PostJDBCTemplate postJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate) {
        this.threadJDBCTemplate = threadJDBCTemplate;
        this.postJDBCTemplate = postJDBCTemplate;
        this.voiceJDBCTemplate = voiceJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
    }

    @RequestMapping(value = "/{slug_or_id}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug_or_id") String slug, @RequestBody List<Post> posts) throws IOException {
        try {
            Thread thread;
            try {
                final int a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
            if (thread == null) {
                return ResponseEntity.notFound().build();
            }
            for (Post post : posts) {
                if (userJDBCTemplate.getUserByNickname(post.getAuthor()) == null) {
                    return ResponseEntity.notFound().build();
                }
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
                post.setCreated(thread.getCreated());
                if (post.getParent() != 0) {
                    final Post parent = postJDBCTemplate.getPostById(post.getParent());
                    post.setForum(thread.getForum());
                    post.setThread(thread.getId());
                    if (parent == null || thread.getId() != parent.getThread()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
        postJDBCTemplate.createPosts(posts);
        return ResponseEntity.status(HttpStatus.CREATED).body(posts);
    }


    @RequestMapping(value = "/{slug_or_id}/vote", method = RequestMethod.POST)
    public ResponseEntity<?> createVoice(@PathVariable(value = "slug_or_id") String slug, @RequestBody Voice voice) throws IOException {
        Thread thread = null;
        try {
            final int a = Integer.parseInt(slug);
            thread = threadJDBCTemplate.getThreadById(a);
        } catch (NumberFormatException ignored) {
            thread = threadJDBCTemplate.getThreadBySlug(slug);
        }
        final User user = userJDBCTemplate.getUserByNickname(voice.getNickname());
        if (thread == null || user == null) {
            return ResponseEntity.notFound().build();
        }
        voice.setThread_id(thread.getId());
        Voice voice1 = voiceJDBCTemplate.getVoiceWithNicknameAndThread(voice.getNickname(), thread.getId());
        if (voice1 == null) {
            voiceJDBCTemplate.createVoice(voice);
            if (voice.getVoice() == 1) {
                thread.setVotes(threadJDBCTemplate.updateVoiceById(thread.getId(), thread.getVotes() + 1));
            } else {
                thread.setVotes(threadJDBCTemplate.updateVoiceById(thread.getId(), thread.getVotes() - 1));
            }
        } else {
            if (voice1.getVoice() != voice.getVoice()) {
                voiceJDBCTemplate.updateVoice(voice);
                if (voice.getVoice() == 1) {
                    thread.setVotes(threadJDBCTemplate.updateVoiceById(thread.getId(), thread.getVotes() + 2));
                } else {
                    thread.setVotes(threadJDBCTemplate.updateVoiceById(thread.getId(), thread.getVotes() - 2));
                }
            }
        }
        LOGGER.debug(voice.toString());
        LOGGER.debug(thread.toString());
        return ResponseEntity.ok(thread);
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getThreads(@PathVariable(value = "slug_or_id") String slug) throws IOException {
        Thread thread;
        try {
            final int id = Integer.parseInt(slug);
            thread = threadJDBCTemplate.getThreadById(id);
        } catch (NumberFormatException ignored) {
            thread = threadJDBCTemplate.getThreadBySlug(slug);
        }
        if (thread != null) {
            return ResponseEntity.ok(thread);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.POST)
    public ResponseEntity<?> updateThreads(@PathVariable(value = "slug_or_id") String
                                                   slug, @RequestBody ThreadUpdate threadUpdate) throws IOException {
        Thread thread;
        try {
            final int a = Integer.parseInt(slug);
            thread = threadJDBCTemplate.getThreadById(a);
        } catch (NumberFormatException ignored) {
            thread = threadJDBCTemplate.getThreadBySlug(slug);
        }
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }
        final Thread newThread = threadJDBCTemplate.updateThread(threadUpdate, thread.getSlug());
        return ResponseEntity.ok(newThread);
    }

    @RequestMapping(path = "/{slug_or_id}/posts", method = RequestMethod.GET)
    public ResponseEntity postsThread(@PathVariable(name = "slug_or_id") String slug,
                                      @RequestParam(name = "limit", required = false) Integer limit,
                                      @RequestParam(name = "marker", required = false, defaultValue = "0") String marker,
                                      @RequestParam(name = "sort", required = false) String sort,
                                      @RequestParam(name = "desc", required = false) Boolean desc) {
        Thread thread;
        try {
            final int a = Integer.parseInt(slug);
            thread = threadJDBCTemplate.getThreadById(a);
        } catch (NumberFormatException ignored) {
            thread = threadJDBCTemplate.getThreadBySlug(slug);
        }

        if (thread == null) {
            return ResponseEntity.notFound().build();
        }
        if (sort == null) {
            sort = "flat";
        }
        if (desc == null) {
            desc = false;
        }
        final ResponsePosts responsePosts = new ResponsePosts();
        try {
            final List<Post> posts = postJDBCTemplate.getByThread(thread.getId(), limit, marker, sort, desc);
            responsePosts.setMarker("some marker");
            responsePosts.setPosts(posts);
            return ResponseEntity.ok(responsePosts);
        } catch (RuntimeException e){
            LOGGER.error(e);
            return ResponseEntity.notFound().build();
        }
    }
}
