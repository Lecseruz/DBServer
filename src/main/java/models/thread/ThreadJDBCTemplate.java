package models.thread;

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

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS thread";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    public void create(Thread thread) {
        String SQL;
        int id = 0;
        if (thread.getCreated() != null) {
            SQL = "insert into Thread ( title, author, forum, message, votes, slug, created) values (?, ?, ?, ?, ?, ?, ?) RETURNING id";
             id = jdbcTemplate.queryForObject(SQL, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug(), ThreadMapper.toTimestamp(thread.getCreated()));
        } else{
            SQL = "insert into Thread ( title, author, forum, message, votes, slug) values (?, ?, ?, ?, ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(SQL, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug());
        }
        LOGGER.debug("created" + thread.getTitle() + " with user ");
        thread.setId(id);
    }

    public Thread getThreadById(int id) {
        String SQL = "select * from thread where id = ?";
        Thread thread = jdbcTemplate.queryForObject(SQL, new Object[] { id }, new ThreadMapper());
        LOGGER.debug("getThreadById success" );
        return thread;
    }

    public Thread getThreadBySlug(String slug) {
        String SQL = "select * from thread where lower(slug) = lower(?)";
        Thread thread = jdbcTemplate.queryForObject(SQL, new Object[] { slug }, new ThreadMapper());
        LOGGER.debug("getThreadById success" );
        return thread;
    }

    public List<Thread> getThreads(String slug, boolean desc, int limit, String timestamp){
        String SQL = "select * from thread where LOWER(forum) = LOWER(?)";
        List<Thread> threads = null;
        if (!timestamp.equals("")){
            if (desc){
                SQL += "AND created <= ?";
            } else {
                SQL += "AND created >= ?";
            }
        }
        if (desc){
            SQL += " order by created desc";
        } else {
            SQL += " order by created";
        }
        if (limit > 0){
            SQL += " limit " + limit;
        }
        if (!timestamp.isEmpty()){
            threads = jdbcTemplate.query(SQL, new ThreadMapper(), slug, ThreadMapper.toTimestamp(timestamp));
        } else {
            threads = jdbcTemplate.query(SQL, new ThreadMapper(), slug);
        }
        LOGGER.debug("getThreads success");
        return threads;
    }

    public void delete() {
        String SQL = "delete from thread";
        jdbcTemplate.update(SQL);
        LOGGER.debug("Deleted Record" );
    }

    public int getCount() {
        String SQL = "select COUNT(*) from thread";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("getVoice success");
        return count;
    }
    public int updateVoice(String slug, int count){
        String SQL = "update thread set votes = ? where lower(slug) = lower(?) RETURNING votes";
        int voice = jdbcTemplate.queryForObject(SQL, Integer.class, count, slug);
        return voice;
    }
}
