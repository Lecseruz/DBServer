package application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by magomed on 20.03.17.
 */
public class Status {
    @JsonProperty
    private int user;
    @JsonProperty
    private int forum;
    @JsonProperty
    private int thread;
    @JsonProperty
    private int post;

    @JsonCreator
    public Status(int user, int forum, int thread, int post) {
        this.forum = forum;
        this.thread = thread;
        this.post = post;
        this.user = user;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getForum() {
        return forum;
    }

    public void setForum(int forum) {
        this.forum = forum;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }
}
