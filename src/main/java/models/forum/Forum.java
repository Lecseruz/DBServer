package models.forum;

/**
 * Created by magomed on 12.03.17.
 */
public class Forum {
    private String title;
    private String nickname;
    private String slag;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSlag() {
        return slag;
    }

    public void setSlag(String slag) {
        this.slag = slag;
    }

    public int getPosts() {
        return posts;
    }

    public void print(){
        System.out.print(title + nickname + slag + posts + thread);
    }
    public void setPosts(int posts) {
        this.posts = posts;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }
}
