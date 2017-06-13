package models.voice;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by magomed on 26.04.17.
 */
public class Voice {
    @JsonProperty("nickaname")
    private String author;

    private int voice;

    private int thread;

    public String getAuthor() {
        return author;
    }

    public void setNickname(String author) {
        this.author = author;
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
                "nickname='" + author + '\'' +
                ", voice=" + voice +
                '}';
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int id) {
        this.thread = id;
    }
}
