package models.forum;

import com.zetcode.conf.AppConfig;

/**
 * Created by magomed on 19.03.17.
 */
public class MainForumApp {
    public static void main(String[] args) {

        Forum forum = new Forum();
        forum.setSlag("dsfsdfsd");
        forum.setTitle("asdas");
        forum.setThread(5);
        forum.setNickname("magsa");
        forum.setPosts(66);
        ForumJDBCTemplate forumJDBCTemplate = new ForumJDBCTemplate(AppConfig.primaryDataSource());
        forumJDBCTemplate.create(forum.getTitle(), forum.getNickname(), forum.getSlag(), forum.getPosts(), forum.getThread());
    }
}
