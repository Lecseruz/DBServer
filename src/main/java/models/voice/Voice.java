package models.voice;

/**
 * Created by magomed on 26.04.17.
 */
public class Voice {
    private String author;

    private int voice;

    private int thread_id;

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

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int id) {
        this.thread_id = id;
    }
}
