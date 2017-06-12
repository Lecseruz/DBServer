package models.ResponseObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.forum.Forum;
import models.post.Post;
import models.thread.Thread;
import models.user.User;
import org.jetbrains.annotations.Nullable;

/**
 * Created by magomed on 08.05.17.
 */
public class ResponseInfoPost {
    @JsonProperty
    private Post post;
    @JsonProperty
    private User author;
    @JsonProperty
    private Thread thread;
    @JsonProperty
    private Forum forum;

    public ResponseInfoPost(Post post, User user, Thread thread, Forum forum) {
        this.post = post;
        this.author = user;
        this.thread = thread;
        this.forum = forum;
    }

    public ResponseInfoPost(){

    }

    public Post getPost() {
        return post;
    }

    public void setPost(@Nullable Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(@Nullable Thread thread) {
        this.thread = thread;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
}
