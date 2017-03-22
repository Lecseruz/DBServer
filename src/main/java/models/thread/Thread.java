package models.thread;

import java.sql.Timestamp;

/**
 * Created by magomed on 18.03.17.
 */
public class Thread {
    private int id;
    private String title;
    private String author;
    private String Forum;
    private String message;
    private int votes;
    private String slug;
    private Timestamp created;

    public Thread(int id, String title, String author, String forum, String message, int votes, String slug, String createdж) {
        this.id = id;
        this.title = title;
        this.author = author;
        Forum = forum;
        this.message = message;
        this.votes = votes;
        this.slug = slug;
        this.created = created;
    }

    public Thread(){
        ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getForum() {
        return Forum;
    }

    public void setForum(String forum) {
        Forum = forum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }
}
