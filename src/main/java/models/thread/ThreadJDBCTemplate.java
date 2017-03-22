package models.thread;

import models.forum.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.management.timer.TimerMBean;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ThreadJDBCTemplate implements ThreadDAO {

    private JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    @Autowired
    public ThreadJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createTable() {
        String query = new StringBuilder()
                .append("CREATE EXTENSION IF NOT EXISTS citext; ")
                .append("CREATE TABLE IF NOT EXISTS thread ( ")
                .append("id SERIAL PRIMARY KEY, ")
                .append("title VARCHAR(128) NOT NULL, ")
                .append("author CITEXT NOT NULL, ")
                .append("forum CITEXT NOT NULL, ")
                .append("message TEXT NOT NULL, ")
                .append("votes BIGINT NOT NULL DEFAULT 0, ")
                .append("slug CITEXT UNIQUE, ")
                .append("created TIMESTAMP NOT NULL DEFAULT current_timestamp, ")
                .append("FOREIGN KEY (author) REFERENCES m_user(nickname)); ")
                .toString();
        LOGGER.debug( query + "success");

        jdbcTemplate.execute(query);
    }

    @Override
    public void dropTable() {
        String query = "DROP TABLE IF EXISTS thread";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    @Override
    public void create(Integer id, String title, String author, String forum, String message, Integer votes, String slug, Timestamp created) {
        String SQL = "insert into Thread (id, title, author, forum, message, votes, slug, created) values (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, id, title, author, forum, message, votes, slug, created);
        LOGGER.debug("created" + title + " with user ");
    }

    @Override
    public Thread getThread(Integer id) {
        String SQL = "select * from thread where id = ?";
        Thread thread = jdbcTemplate.queryForObject(SQL, new Object[] { id }, new ThreadMapper());
        System.out.println("getThread success" );
        return thread;
    }

    @Override
    public void delete() {
        String SQL = "delete from thread";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }

    @Override
    public int getCount() {
        String SQL = "select COUNT(*) from thread";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("getCount success");
        return count;
    }
}
