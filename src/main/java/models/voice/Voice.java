package models.voice;

/**
 * Created by magomed on 26.04.17.
 */
public class Voice {
    private String nickname;

    private int voice;

    private int thread_id;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    @Override
    public String toString() {
        return "Voice{" +
                "nickname='" + nickname + '\'' +
                ", voice=" + voice +
                '}';
    }

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int thread_id) {
        this.thread_id = thread_id;
    }
}
