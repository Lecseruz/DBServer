package models.thread;

import config.TimestampHelper;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class ThreadJDBCTemplate {

    private JdbcTemplate jdbcTemplate;

//    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    @Autowired
    public ThreadJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearTable() {
        final String query = "TRUNCATE TABLE thread CASCADE ";

        jdbcTemplate.execute(query);
//        LOGGER.debug("drop table success");

    }

    public void create(Thread thread) {
        String sql;
        final int id;
        if (thread.getCreated() != null) {
            sql = "INSERT INTO Thread ( title, author, forum, message, votes, slug, created) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(sql, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug(), TimestampHelper.toTimestamp(thread.getCreated()));
        } else {
            sql = "INSERT INTO Thread ( title, author, forum, message, votes, slug) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(sql, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getVotes(), thread.getSlug());
        }
        sql =
                "UPDATE forum SET threads = threads + 1 " +
                        "WHERE slug = ? ;";
        jdbcTemplate.update(sql, thread.getForum());
//        LOGGER.debug("created" + thread.getTitle() + " with user ");
        thread.setId(id);
    }

    public @Nullable Thread updateThread(ThreadUpdate threadUpdate, String slug) {
        if (threadUpdate.getMessage() != null || threadUpdate.getTitle() != null) {
            String sql = "UPDATE thread SET ";
            if (threadUpdate.getTitle() != null) {
                sql += "title = '" + threadUpdate.getTitle() + "' ";
            }
            if (threadUpdate.getMessage() != null) {
                if (threadUpdate.getTitle() != null) {
                    sql += ", ";
                }
                sql += "message = '" + threadUpdate.getMessage() + "' ";
            }
            sql += "WHERE lower(slug) = lower(?)";
            jdbcTemplate.update(sql, slug);
        }
//        LOGGER.debug("updateThread success");
        return getThreadBySlug(slug);
    }

    public @Nullable Thread getThreadById(int id) {
        try {
            final String sql = "SELECT * FROM thread WHERE id = ?";
            //            LOGGER.debug("getThreadById success");
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ThreadMapper());
        } catch (EmptyResultDataAccessException e) {
//            LOGGER.error(e);
            return null;
        }
    }

    public @Nullable Thread getThreadBySlug(String slug) {
        try {
            final String sql = "SELECT * FROM thread WHERE lower(slug) = lower(?)";
            //            LOGGER.debug("getThreadById success");
            return jdbcTemplate.queryForObject(sql, new Object[]{slug}, new ThreadMapper());
        } catch (EmptyResultDataAccessException e) {
//            LOGGER.error(e);
            return null;
        }
    }

    public List<Thread> getThreads(String slug, boolean desc, int limit, String timestamp) {
        String sql = "select * from thread where LOWER(forum) = LOWER(?)";
        if (!timestamp.isEmpty()) {
            if (desc) {
                sql += "AND created <= ?";
            } else {
                sql += "AND created >= ?";
            }
        }
        if (desc) {
            sql += " order by created desc";
        } else {
            sql += " order by created";
        }
        if (limit > 0) {
            sql += " limit " + limit;
        }
        final List<Thread> threads;
        if (!timestamp.isEmpty()) {
            threads = jdbcTemplate.query(sql, new ThreadMapper(), slug, TimestampHelper.toTimestamp(timestamp));
        } else {
            threads = jdbcTemplate.query(sql, new ThreadMapper(), slug);
        }
//        LOGGER.debug("getThreads success");
        return threads;
    }

    public int getCount() {
        final String sql = "SELECT COUNT(*) FROM thread";
        //        LOGGER.debug("getVoiceWithNickname success");
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int updateVoiceById(int id, int count) {
        final String sql = "UPDATE thread SET votes = ? WHERE id = ? RETURNING votes";
        //        LOGGER.debug("updateVoiceById success");
        return jdbcTemplate.queryForObject(sql, Integer.class, count, id);
    }
}
