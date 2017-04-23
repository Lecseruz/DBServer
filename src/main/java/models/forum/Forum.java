package models.forum;

/**
 * Created by magomed on 12.03.17.
 */
public class Forum {
    private String title;
    private String user;
    private String slug;
    private int posts;
    private int thread;

    public int getThread() {
        return thread;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getPosts() {
        return posts;
    }

    public void print(){
        System.out.print(title + user + slug + posts + thread);
    }
    public void setPosts(int posts) {
        this.posts = posts;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }
}
