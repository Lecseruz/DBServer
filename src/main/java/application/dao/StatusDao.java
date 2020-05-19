package application.dao;

import application.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by magomed on 20.03.17.
 */

@Service
@Transactional
public class StatusDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;
    private final ForumDao forumDao;
    private final PostDao postDao;
    private final ThreadDao threadDao;
    private final VoiceDao voiceDao;
//    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(StatusJDBCTemplate.class);


    @Autowired
    public StatusDao(VoiceDao voiceDao, JdbcTemplate jdbcTemplate, UserDao userDao, ForumDao forumDao, PostDao postDao, ThreadDao threadDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.forumDao = forumDao;
        this.threadDao = threadDao;
        this.postDao = postDao;
        this.voiceDao = voiceDao;
    }

    public Status getStatus() {
        return new Status(userDao.getCount(), forumDao.getCount(),  threadDao.getCount(), postDao.getCount());
    }

    public void clearTable() {
        postDao.clearTable();
        forumDao.clearTable();
        threadDao.clearTable();
        userDao.clearTable();
        voiceDao.clearTable();
    }
}
