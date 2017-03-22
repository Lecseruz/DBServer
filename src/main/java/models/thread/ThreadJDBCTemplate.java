package models.thread;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.management.timer.TimerMBean;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * Created by magomed on 19.03.17.
 */
public class ThreadJDBCTemplate implements ThreadDAO {

    private JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    public ThreadJDBCTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(Integer id, String title, String author, String forum, String message, Integer votes, String slug, Timestamp created) {
        String SQL = "insert into Thread (id, title, author, forum, message, votes, slug, created) values (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, id, title, author, forum, message, votes, slug, created);
        LOGGER.debug("created" + title + " with user ");
    }

    @Override
    public List<Thread> listThread() {
        return null;
    }

    @Override
    public Thread getThread(Integer id) {
        String SQL = "select * from Forum where id = ?";
        List <Thread> forums = jdbcTemplate.query(SQL, new ThreadMapper(), id);
        if (!forums.isEmpty()){
            return forums.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void delete() {
        String SQL = "delete from thread";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }

    @Override
    public void update(Integer id, Integer age) {

    }
}
