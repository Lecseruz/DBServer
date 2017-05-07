package models.post;

import config.TimestampHelper;
/**
 * Created by magomed on 19.03.17.
 */
public class Post implements Comparable<Post> {
    private int id;
    private int parent;
    private String author;
    private String message;
    private boolean isEdited;
    private String forum;
    private int thread;
    private String created;

    public Post(){

    }
    public Post(int id, int parent, String author, String message, boolean isEdited, String forum, int thread, String created) {
        this.id = id;
        this.parent = parent;
        this.author = author;
        this.message = message;
        this.isEdited = isEdited;
        this.forum = forum;
        this.thread = thread;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        isEdited = isEdited;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public int compareTo(Post o) {
        if (TimestampHelper.toTimestamp(created).getTime() == TimestampHelper.toTimestamp(o.created).getTime()){
            return 0;
        } else if (TimestampHelper.toTimestamp(created).getTime() == TimestampHelper.toTimestamp(o.created).getTime()){
            return 1;
        } else {
            return -1;
        }
    }
}
