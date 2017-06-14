package application.models.thread;

import application.config.TimestampHelper;
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

    public @Nullable Thread getThreadBySlugOrId(String slug){
        if (slug.matches("[-+]?\\d*\\.?\\d+")) {
            return getThreadById(Integer.parseInt(slug));
        } else {
            return getThreadBySlug(slug);
        }
    }

    public void create(Thread thread) {
        String sql;
        final int id;
        if (thread.getCreated() != null) {
            sql = "INSERT INTO Thread ( title, user_id, forum_id, message, slug, created) VALUES (?," +
                    " (SELECT id FROM m_user m WHERE LOWER(m.nickname COLLATE \"ucs_basic\") = LOWER(? COLLATE \"ucs_basic\"))," +
                    " (SELECT id FROM forum f WHERE LOWER(f.slug) = LOWER(?))," +
                    " ?, ?, ?) RETURNING id";
            id = jdbcTemplate.queryForObject(sql, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getSlug(), TimestampHelper.toTimestamp(thread.getCreated()));
        } else {
            sql = "INSERT INTO Thread ( title, user_id, forum_id, message, slug) VALUES (?," +
                    " (SELECT id FROM m_user m WHERE LOWER(m.nickname COLLATE \"ucs_basic\") = LOWER(? COLLATE \"ucs_basic\"))," +
                    " (SELECT id FROM forum f WHERE LOWER(f.slug) = LOWER(?))," +
                    " ?,  ?) RETURNING id";
            id = jdbcTemplate.queryForObject(sql, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getSlug());
        }
        sql =
                "UPDATE forum SET threads = threads + 1 " +
                        "WHERE LOWER(slug) = LOWER(?) ;";
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
            sql += "WHERE LOWER(slug) = LOWER(?)";
            jdbcTemplate.update(sql, slug);
        }
//        LOGGER.debug("updateThread success");
        return getThreadBySlug(slug);
    }

    public @Nullable Thread getThreadById(int id) {
        try {
            final String sql = "SELECT t.*, m.nickname, f.slug AS forum_slug FROM thread t " +
                    " JOIN forum f ON (t.forum_id = f.id)" +
                    " JOIN m_user m ON (m.id = t.user_id)" +
                    " WHERE t.id = ?";
            //            LOGGER.debug("getThreadById success");
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ThreadMapper());
        } catch (EmptyResultDataAccessException e) {
//            LOGGER.error(e);
            return null;
        }
    }

    public @Nullable Thread getThreadBySlug(String slug) {
        try {
            final String sql = "SELECT t.*, m.nickname, f.slug AS forum_slug FROM thread t " +
                    " JOIN forum f ON (t.forum_id = f.id) " +
                    " JOIN m_user m ON (m.id = t.user_id)" +
                    " WHERE LOWER(t.slug) = LOWER(?)";
            //            LOGGER.debug("getThreadById success");
            return jdbcTemplate.queryForObject(sql, new Object[]{slug}, new ThreadMapper());
        } catch (EmptyResultDataAccessException e) {
//            LOGGER.error(e);
            return null;
        }
    }

    public List<Thread> getThreads(int id, boolean desc, int limit, String timestamp) {
        String sql = "select m.nickname, t.*, f.slug AS forum_slug from thread t" +
                " JOIN forum f ON (t.forum_id = f.id AND f.id = ?) " +
                " JOIN m_user m ON (m.id = t.user_id) ";
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
//        LOGGER.debug("getThreads success");
        return threads;
    }

    public int getCount() {
        final String sql = "SELECT COUNT(*) FROM thread";
        //        LOGGER.debug("getVoiceWithNickname success");
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}
