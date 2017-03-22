package models.status;

/**
 * Created by magomed on 20.03.17.
 */
public class Status {
    private int count_user;
    private int count_forum;
    private int count_thread;
    private int count_post;

    public Status() {
    }

    public int getCount_post() {
        return count_post;
    }

    public void setCount_post(int count_post) {
        this.count_post = count_post;
    }

    public int getCount_thread() {
        return count_thread;
    }

    public void setCount_thread(int count_thread) {
        this.count_thread = count_thread;
    }

    public int getCount_forum() {
        return count_forum;
    }

    public void setCount_forum(int count_forum) {
        this.count_forum = count_forum;
    }

    public int getCount_user() {
        return count_user;
    }

    public void setCount_user(int count_user) {
        this.count_user = count_user;
    }
}
