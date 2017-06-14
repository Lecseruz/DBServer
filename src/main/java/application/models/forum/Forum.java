package application.models.forum;

/**
 * Created by magomed on 12.03.17.
 */
public class Forum {
    int id;
    private String title;
    private String user;
    private String slug;
    private int posts;
    private int threads;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getThreads() {
        return threads;
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

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public void setThreads(int thread) {
        this.threads = thread;
    }
}
