package controller;

import models.ResponseObject.ResponsePosts;
import models.post.Post;
import models.post.PostJDBCTemplate;
import models.status.StatusJDBCTemplate;
import models.thread.Thread;
import models.thread.ThreadJDBCTemplate;
import models.thread.ThreadUpdate;
import models.voice.Voice;
import models.user.UserJDBCTemplate;
import models.voice.VoiceJDBCTemplate;
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
            Thread thread = null;
            try {
                final int a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
            for (Post post : posts) {
                userJDBCTemplate.getUserByNickname(post.getAuthor());
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
                post.setCreated(thread.getCreated());
                if(post.getParent() != 0) {
                    Post parent = postJDBCTemplate.getPostById(post.getParent());
                    post.setForum(thread.getForum());
                    post.setThread(thread.getId());
                    if (parent == null || thread.getId() != parent.getThread()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
                    }
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
        }
        postJDBCTemplate.createPosts(posts);
        return ResponseEntity.status(HttpStatus.CREATED).body(posts);
    }


    @RequestMapping(value = "/{slug_or_id}/vote", method = RequestMethod.POST)
    public ResponseEntity<?> createVoice(@PathVariable(value = "slug_or_id") String slug, @RequestBody Voice voice) throws IOException {
        Thread thread = null;
        try {
            try {
                final int a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
            voiceJDBCTemplate.createVoice(voice);
            userJDBCTemplate.getUserByNickname(voice.getNickname());
            if (voice.getVoice() == 1)
                thread.setVotes(threadJDBCTemplate.updateVoice(thread.getSlug(), thread.getVotes() + 1));
            else {
                thread.setVotes(threadJDBCTemplate.updateVoice(thread.getSlug(), thread.getVotes() - 1));
            }
            return ResponseEntity.status(HttpStatus.OK).body(thread);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (DuplicateKeyException e) {
            if (!(voice.getVoice() == voiceJDBCTemplate.getVoiceWithNickname(voice.getNickname()).getVoice())) {
                if (voice.getVoice() == 1) {
                    assert thread != null;
                    thread.setVotes(threadJDBCTemplate.updateVoice(thread.getSlug(), thread.getVotes() + 2));
                    voiceJDBCTemplate.updateVoice(voice.getVoice(), voice.getNickname());
                } else {
                    assert thread != null;
                    thread.setVotes(threadJDBCTemplate.updateVoice(thread.getSlug(), thread.getVotes() - 2));
                    voiceJDBCTemplate.updateVoice(voice.getVoice(), voice.getNickname());
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(thread);
        }
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getThreads(@PathVariable(value = "slug_or_id") String slug) throws IOException {
        try {
            Thread thread;
            try {
                final int a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
            return ResponseEntity.ok(thread);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.POST)
    public ResponseEntity<?> updateThreads(@PathVariable(value = "slug_or_id") String slug, @RequestBody ThreadUpdate threadUpdate) throws IOException {
        Thread thread;
        try {
            final int a = Integer.parseInt(slug);
            thread = threadJDBCTemplate.getThreadById(a);
        } catch (NumberFormatException ignored) {
            thread = threadJDBCTemplate.getThreadBySlug(slug);
        }
        threadJDBCTemplate.updateThread(threadUpdate, thread.getSlug());
        thread.setTitle(threadUpdate.getTitle());
        thread.setMessage(threadUpdate.getMessage());
        return ResponseEntity.ok(thread);
    }

    @RequestMapping(path = "/{slug_or_id}/posts", method = RequestMethod.GET)
    public ResponseEntity postsThread(@PathVariable(name = "slug_or_id") String slug,
                                      @RequestParam(name = "limit", required = false) Integer limit,
                                      @RequestParam(name = "marker", required = false, defaultValue = "0") String marker,
                                      @RequestParam(name = "sort", required = false) String sort,
                                      @RequestParam(name = "desc", required = false) Boolean desc) {
        Thread thread;
        try {
            try {
                final int a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (sort == null) {
            sort = "flat";
        }
        if (desc == null) {
            desc = false;
        }
        final ResponsePosts responsePosts = new ResponsePosts();
        final List<Post> posts = postJDBCTemplate.getByThread(thread.getId(), limit, marker, sort, desc);

        responsePosts.setMarker("some marker");
        responsePosts.setPosts(posts);
        return ResponseEntity.status(HttpStatus.OK).body(responsePosts);
    }
}
