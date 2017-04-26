package controller;

import models.forum.ForumJDBCTemplate;
import models.post.Post;
import models.post.PostJDBCTemplate;
import models.status.StatusJDBCTemplate;
import models.thread.Thread;
import models.thread.ThreadJDBCTemplate;
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
    private int id = 0;
    private final ForumJDBCTemplate forumJDBCTemplate;
    private final StatusJDBCTemplate statusJDBCTemplate;
    private final UserJDBCTemplate userJDBCTemplate;
    private final ThreadJDBCTemplate threadJDBCTemplate;
    private final PostJDBCTemplate postJDBCTemplate;
    private final VoiceJDBCTemplate voiceJDBCTemplate;

    @Autowired
    public ThreadController(ForumJDBCTemplate forumJDBCTemplate, VoiceJDBCTemplate voiceJDBCTemplate, PostJDBCTemplate postJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate, StatusJDBCTemplate statusJDBCTemplate, UserJDBCTemplate userJDBCTemplate) {
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.statusJDBCTemplate = statusJDBCTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
        this.postJDBCTemplate = postJDBCTemplate;
        this.voiceJDBCTemplate = voiceJDBCTemplate;
    }

    @RequestMapping(value = "/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity<?> createThread(@PathVariable(value = "slug") String slug, @RequestBody List<Post> posts) throws IOException {
        try {

            Thread thread = null;
            try {
                int a = Integer.parseInt(slug);
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



    @RequestMapping(value = "/{slug}/vote", method = RequestMethod.POST)
    public ResponseEntity<?> createVoice(@PathVariable(value = "slug") String slug, @RequestBody Voice voice) throws IOException {
        Thread thread = null;
        try {
            try {
                int a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
            voiceJDBCTemplate.createVoice(voice);
            if (voice.getVoice() == 1)
                thread.setVotes(threadJDBCTemplate.updateVoice(thread.getSlug(), thread.getVotes() + 1));
            else {
                thread.setVotes(threadJDBCTemplate.updateVoice(thread.getSlug(), thread.getVotes() - 1));
            }
            return ResponseEntity.status(HttpStatus.OK).body(thread);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (DuplicateKeyException e){
            if (!(voice.getVoice() == voiceJDBCTemplate.getVoiceWithNickname(voice.getNickname()).getVoice())){
                if (voice.getVoice() == 1){
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

    @RequestMapping(value = "/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity<?> getThread(@PathVariable(value = "slug") String slug) throws IOException {
        Thread thread = null;
        try {
            try {
                int a = Integer.parseInt(slug);
                thread = threadJDBCTemplate.getThreadById(a);
            } catch (NumberFormatException ignored) {
                thread = threadJDBCTemplate.getThreadBySlug(slug);
            }
            return ResponseEntity.status(HttpStatus.OK).body(thread);
        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
