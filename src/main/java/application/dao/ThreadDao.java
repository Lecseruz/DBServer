package application.dao;

import application.config.TimestampHelper;
import application.dao.mapper.ThreadMapper;
import application.models.Thread;
import application.models.ThreadUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ThreadDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ThreadDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearTable() {
        final String query = "TRUNCATE TABLE thread CASCADE ";

        jdbcTemplate.execute(query);
    }

    public void create(Thread thread) {
        String sql;
        final int id;
        if (thread.getCreated() != null) {
            sql = "INSERT INTO Thread ( title, user_id, forum_id, message, slug, __nickname, created) VALUES (?," +
                    " (SELECT id FROM m_user m WHERE LOWER(m.nickname COLLATE \"ucs_basic\") = LOWER(? COLLATE \"ucs_basic\"))," +
                    " (SELECT id FROM forum f WHERE LOWER(f.slug) = LOWER(?))," +
                    " ?, ?, ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(sql, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getSlug(), thread.getAuthor(), TimestampHelper.toTimestamp(thread.getCreated()));
        } else {
            sql = "INSERT INTO Thread ( title, user_id, forum_id, message, slug, __nickname) VALUES (?," +
                    " (SELECT id FROM m_user m WHERE LOWER(m.nickname COLLATE \"ucs_basic\") = LOWER(? COLLATE \"ucs_basic\"))," +
                    " (SELECT id FROM forum f WHERE LOWER(f.slug) = LOWER(?))," +
                    " ?,  ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(sql, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getSlug(), thread.getAuthor());
        }
        sql =
                "UPDATE forum SET threads = threads + 1 " +
                        "WHERE LOWER(slug) = LOWER(?) ;";
        jdbcTemplate.update(sql, thread.getForum());
        thread.setId(id);
    }

    public Thread updateThread(ThreadUpdate threadUpdate, String slug) {
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
            sql += "WHERE LOWER(slug) = LOWER(?)";
            jdbcTemplate.update(sql, slug);
        }
        return getThreadBySlug(slug);
    }

    public Thread getThreadById(int id) {
        try {
            final String sql = "SELECT t.*, f.slug AS forum_slug FROM thread t " +
                    " JOIN forum f ON (t.forum_id = f.id)" +
                    " WHERE t.id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ThreadMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Thread getThreadBySlug(String slug) {
        try {
            final String sql = "SELECT t.*, f.slug AS forum_slug FROM thread t " +
                    " JOIN forum f ON (t.forum_id = f.id) " +
                    " WHERE LOWER(t.slug) = LOWER(?)";
            return jdbcTemplate.queryForObject(sql, new Object[]{slug}, new ThreadMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Thread> getThreads(int id, boolean desc, int limit, String timestamp) {
        String sql = "select t.*, f.slug AS forum_slug from thread t" +
                " JOIN forum f ON (t.forum_id = f.id) WHERE f.id = ? ";
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
            threads = jdbcTemplate.query(sql, new ThreadMapper(), id, TimestampHelper.toTimestamp(timestamp));
        } else {
            threads = jdbcTemplate.query(sql, new ThreadMapper(), id);
        }
        return threads;
    }

    public int getCount() {
        final String sql = "SELECT COUNT(*) FROM thread";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}
