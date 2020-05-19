package application.service.impl;

import application.models.Thread;
import application.models.Voice;
import application.dao.VoiceDao;
import application.service.api.IVoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoiceService implements IVoiceService {
    private ThreadService threadService;
    private UserService userService;
    private VoiceDao voiceDao;

    @Autowired
    public VoiceService(ThreadService threadService, UserService userService, VoiceDao voiceDao) {
        this.threadService = threadService;
        this.userService = userService;
        this.voiceDao = voiceDao;
    }

    @Override
    public Thread addVoice(String slugOrId, Voice voice) {
        Thread thread = threadService.getThread(slugOrId);
        userService.getUser(voice.getAuthor());
        voice.setThread(thread.getId());
        thread.setVotes(voiceDao.addVote(voice));
        return thread;
    }
}
