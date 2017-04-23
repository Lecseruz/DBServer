package models.status;

import models.forum.ForumJDBCTemplate;
import models.post.PostJDBCTemplate;
import models.thread.ThreadJDBCTemplate;
import models.user.UserJDBCTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by magomed on 20.03.17.
 */

@Service
@Transactional
public class StatusJDBCTemplate implements StatusDAO {
    private final JdbcTemplate jdbcTemplate;
    private final UserJDBCTemplate userJDBCTemplate;
    private final ForumJDBCTemplate forumJDBCTemplate;
    private final PostJDBCTemplate postJDBCTemplate;
    private final ThreadJDBCTemplate threadJDBCTemplate;

    @Autowired
    public StatusJDBCTemplate(JdbcTemplate jdbcTemplate, UserJDBCTemplate userJDBCTemplate, ForumJDBCTemplate forumJDBCTemplate, PostJDBCTemplate postJDBCTemplate, ThreadJDBCTemplate threadJDBCTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userJDBCTemplate = userJDBCTemplate;
        this.forumJDBCTemplate = forumJDBCTemplate;
        this.threadJDBCTemplate = threadJDBCTemplate;
        this.postJDBCTemplate = postJDBCTemplate;
//        dropTable();
//        createTable();
    }

    @Override
    public Status getStatus() {
        return new Status(userJDBCTemplate.getCount(), forumJDBCTemplate.getCount(),  threadJDBCTemplate.getCount(), postJDBCTemplate.getCount());
    }

    @Override
    public void createTable() {
        userJDBCTemplate.createTable();
        forumJDBCTemplate.createTable();
        postJDBCTemplate.createTable();
        threadJDBCTemplate.createTable();
    }

    @Override
    public void dropTable() {
        postJDBCTemplate.dropTable();
        forumJDBCTemplate.dropTable();
        threadJDBCTemplate.dropTable();
        userJDBCTemplate.dropTable();
    }
}
