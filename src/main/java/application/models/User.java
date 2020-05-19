package application.models;

public class User {
    private String nickname;
    private String fullname;
    private String about;
    private String email;

    public User(String nickname, String fullname, String about, String email) {
        this.nickname = nickname;
        this.fullname = fullname;
        this.about = about;
        this.email = email;
    }

    public boolean isEmpty(){
        return fullname == null || about == null ||
                email == null;
    }

    public User(){
        ;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
