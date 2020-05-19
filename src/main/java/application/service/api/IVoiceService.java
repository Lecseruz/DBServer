package application.service.api;

import application.models.Thread;
import application.models.Voice;

public interface IVoiceService {
    Thread addVoice(String slugOrId, Voice voice);
}
