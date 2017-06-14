package application.models.post;

import application.config.TimestampHelper;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
@Transactional
public class PostJDBCTemplate {

    private final JdbcTemplate jdbcTemplate;
    //    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    @Autowired
    public PostJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearTable() {
        final String sql = "TRUNCATE TABLE post CASCADE ";

        jdbcTemplate.execute(sql);
//        LOGGER.debug("drop table success");

    }

    public void createPosts(List<Post> posts) {

        final Timestamp created = new Timestamp(System.currentTimeMillis());
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String sql = "INSERT INTO post (id, parent_id, user_id, message, isEdited, forum_id, thread, created, path) VALUES (?, ?, " +
                "(SELECT id FROM m_user m WHERE LOWER(m.nickname) = LOWER(?)), ?, ?, " +
                "(SELECT id FROM forum WHERE LOWER(slug) = LOWER(?)), ?, ?, array_append((SELECT path FROM post WHERE id = ?), ?))";
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.NO_GENERATED_KEYS)) {
            for (Post post : posts) {
                post.setId(jdbcTemplate.queryForObject("SELECT nextval(pg_get_serial_sequence('post', 'id'))", (rs, rowNum) -> rs.getInt("nextval")));
                post.setCreated(TimestampHelper.fromTimestamp(created));
                pst.setInt(1, post.getId());
                pst.setInt(2, post.getParent());
                pst.setString(3, post.getAuthor());
                pst.setString(4, post.getMessage());
                pst.setBoolean(5, post.getIsEdited());
                pst.setString(6, post.getForum());
                pst.setInt(7, post.getThread());
                pst.setTimestamp(8, created);
                pst.setInt(9, post.getParent());
                pst.setInt(10, post.getId());
                pst.addBatch();
//                LOGGER.info("Post with id \"{}\" created", post.getId());
            }
            pst.executeBatch();
            sql = "UPDATE forum SET posts = posts + ? " +
                    "WHERE slug = ? ;";
            jdbcTemplate.update(sql, posts.size(), posts.get(0).getForum());
            //        LOGGER.debug("create posts with user ");
        } catch (SQLException e) {
            e.getNextException().printStackTrace();
        }
    }

    public int getCount() {
        final String sql = "SELECT COUNT(*) FROM post";
        //        LOGGER.debug("getVoiceWithNickname success");
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<Post> flatSort(int id, int limit, int offset, boolean desc) {
        final String sql = "SELECT p.*, m.nickname, f.slug as forum_slug FROM post p " +
                " JOIN forum f ON (p.forum_id = f.id)" +
                " JOIN m_user m ON (m.id = p.user_id)" +
                " WHERE p.thread = ?" +
                "ORDER BY p.created " + (desc ? "DESC" : "ASC") + ", p.id " + (desc ? "DESC" : "ASC") + " LIMIT ? OFFSET ?";

        //        LOGGER.debug("get posts success");
        return jdbcTemplate.query(sql, new PostMapper(), id, limit, offset);
    }

    public List<Post> treeSort(int id, int limit, int offset, boolean desc) {
        final String sql = "SELECT p.*, m.nickname, f.slug as forum_slug FROM post p " +
                " JOIN forum f ON (p.forum_id = f.id)" +
                " JOIN m_user m ON (m.id = p.user_id)" +
                "WHERE p.thread = ?  " +
                "ORDER BY p.path " + (desc ? "DESC" : "ASC") + " LIMIT ? OFFSET ?";
        //        LOGGER.debug("get posts success");
        return jdbcTemplate.query(sql, new PostMapper(), id, limit, offset);
    }

    public List<Post> parentTreeSort(int id, Boolean desc, List<Integer> parents) {
        final String sql = "SELECT p.*, m.nickname, f.slug as forum_slug FROM post p " +
                " JOIN forum f ON (p.forum_id = f.id)" +
                " JOIN m_user m ON (m.id = p.user_id)"  +
                "WHERE p.thread = ? " + " AND p.path[1] = ? " +
                "ORDER BY p.path " + (desc ? "DESC" : "ASC") + ", p.id " + (desc ? "DESC" : "ASC");
        final List<Post> posts = new ArrayList<>();
        for (Integer parent : parents) {
            posts.addAll(jdbcTemplate.query(sql, new PostMapper(), id, parent));
        }
//        LOGGER.debug("parentTree success");
        return posts;
    }

    public List<Integer> getParents(int id, int offset, int limit, boolean desc) {
        final String sql = "SELECT id FROM post WHERE parent_id = 0 and  thread = ? " +
                "ORDER BY id " + (desc ? "DESC" : "ASC") + " LIMIT ? OFFSET ? ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id"), id, limit, offset);
    }

    public @Nullable Post getPostById(int id) {
        try {
            //            LOGGER.debug("get post by id success");
            final String sql = "SELECT p.*, m.nickname, f.slug AS forum_slug FROM post p " +
                    " JOIN forum f ON (p.forum_id = f.id)" +
                    " JOIN m_user m ON (m.id = p.user_id)" +
                    " WHERE p.id = ?";
            return jdbcTemplate.queryForObject(sql, new PostMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public @Nullable Post updatePost(String message, int id) {
        if (message != null) {
            final String sql = "UPDATE post SET message = ?, isedited = " + true + " WHERE id = ?";
            jdbcTemplate.update(sql, message, id);
//            LOGGER.debug("uodate post success");
        }
        return getPostById(id);
    }
}
