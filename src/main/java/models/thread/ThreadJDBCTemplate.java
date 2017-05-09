package models.thread;

import config.TimestampHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ThreadJDBCTemplate {

    private JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    @Autowired
    public ThreadJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTable() {
        final String query =
                "CREATE EXTENSION IF NOT EXISTS citext; " +
                        "CREATE TABLE IF NOT EXISTS thread ( " +
                        "id SERIAL PRIMARY KEY, " +
                        "title VARCHAR(128) NOT NULL, " +
                        "author CITEXT NOT NULL, " +
                        "forum CITEXT NOT NULL, " +
                        "message TEXT NOT NULL, " +
                        "votes BIGINT NOT NULL DEFAULT 0, " +
                        "slug CITEXT UNIQUE, " +
                        "created TIMESTAMP NOT NULL DEFAULT current_timestamp, " +
                        "FOREIGN KEY (author) REFERENCES m_user(nickname)); ";
        LOGGER.debug(query + "success");

        jdbcTemplate.execute(query);
    }

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS thread CASCADE ";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    public void create(Thread thread) {
        String SQL;
        int id = 0;
        if (thread.getCreated() != null) {
            SQL = "INSERT INTO Thread ( title, author, forum, message, votes, slug, created) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(SQL, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug(), TimestampHelper.toTimestamp(thread.getCreated()));
        } else {
            SQL = "INSERT INTO Thread ( title, author, forum, message, votes, slug) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(SQL, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug());
        }
        String subQuery =
                "UPDATE forum SET threads = threads + 1 " +
                "WHERE slug = ? ;";
        jdbcTemplate.update(subQuery, thread.getForum());
        LOGGER.debug("created" + thread.getTitle() + " with user ");
        thread.setId(id);
    }

    public Thread updateThread(ThreadUpdate threadUpdate, String slug){
        if (threadUpdate.getMessage() != null || threadUpdate.getTitle() != null) {
            String SQL = "UPDATE thread SET ";
            if (threadUpdate.getTitle() != null){
                SQL += "title = '" + threadUpdate.getTitle() + "' ";
            }
            if (threadUpdate.getMessage() != null){
                if (threadUpdate.getTitle() != null){
                    SQL+= ", ";
                }
                SQL += "message = '" + threadUpdate.getMessage() + "' ";
            }
            SQL += "WHERE lower(slug) = lower(?)";
            jdbcTemplate.update(SQL, slug);
        }
        LOGGER.debug("updateThread success");
        return getThreadBySlug(slug);
    }

    public Thread getThreadById(int id) {
        String SQL = "SELECT * FROM thread WHERE id = ?";
        Thread thread = jdbcTemplate.queryForObject(SQL, new Object[]{id}, new ThreadMapper());
        LOGGER.debug("getThreadById success");
        return thread;
    }

    public Thread getThreadBySlug(String slug) {
        String SQL = "SELECT * FROM thread WHERE lower(slug) = lower(?)";
        Thread thread = jdbcTemplate.queryForObject(SQL, new Object[]{slug}, new ThreadMapper());
        LOGGER.debug("getThreadById success");
        return thread;
    }

    public List<Thread> getThreads(String slug, boolean desc, int limit, String timestamp) {
        String SQL = "select * from thread where LOWER(forum) = LOWER(?)";
        List<Thread> threads = null;
        if (!timestamp.equals("")) {
            if (desc) {
                SQL += "AND created <= ?";
            } else {
                SQL += "AND created >= ?";
            }
        }
        if (desc) {
            SQL += " order by created desc";
        } else {
            SQL += " order by created";
        }
        if (limit > 0) {
            SQL += " limit " + limit;
        }
        if (!timestamp.isEmpty()) {
            threads = jdbcTemplate.query(SQL, new ThreadMapper(), slug, TimestampHelper.toTimestamp(timestamp));
        } else {
            threads = jdbcTemplate.query(SQL, new ThreadMapper(), slug);
        }
        LOGGER.debug("getThreads success");
        return threads;
    }

    public void delete() {
        String SQL = "DELETE FROM thread";
        jdbcTemplate.update(SQL);
        LOGGER.debug("Deleted Record");
    }

    public int getCount() {
        String SQL = "SELECT COUNT(*) FROM thread";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("getVoiceWithNickname success");
        return count;
    }

    public int updateVoice(String slug, int count) {
        String SQL = "UPDATE thread SET votes = ? WHERE lower(slug) = lower(?) RETURNING votes";
        int voice = jdbcTemplate.queryForObject(SQL, Integer.class, count, slug);
        LOGGER.debug("updateVoice success");
        return voice;
    }
}
