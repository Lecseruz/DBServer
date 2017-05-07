package models.post;

import config.TimestampHelper;
import models.thread.ThreadJDBCTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PostJDBCTemplate {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    @Autowired
    public PostJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTable() {
        String sql =
                "CREATE EXTENSION IF NOT EXISTS citext; " +
                        "CREATE TABLE IF NOT EXISTS post ( " +
                        "id SERIAL PRIMARY KEY, " +
                        "parent_id BIGINT NOT NULL DEFAULT 0, " +
                        "author CITEXT NOT NULL, " +
                        "message TEXT NOT NULL, " +
                        "isEdited BOOLEAN NOT NULL DEFAULT false, " +
                        "forum CITEXT NOT NULL, " +
                        "thread BIGINT NOT NULL, " +
                        "created TIMESTAMP NOT NULL DEFAULT current_timestamp);" +
                        "CREATE UNIQUE INDEX  ON post (id); " +
                        "CREATE INDEX  ON post (author); " +
                        "CREATE INDEX  ON post (forum); " +
                        "CREATE INDEX  ON post (thread);";

        LOGGER.debug(sql +
                "create table success");

        jdbcTemplate.execute(sql);
    }

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS post";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    public void createPosts(List<Post> posts) {
        String SQL = "INSERT INTO post (parent_id, author, message, isEdited, forum, thread, created) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        String subQuery =
                "UPDATE forum SET posts = posts + 1 " +
                        "WHERE slug = ? ;";

        for (Post post : posts) {
            int id = jdbcTemplate.queryForObject(SQL, Integer.class, post.getParent(), post.getAuthor(), post.getMessage(), post.getIsEdited(), post.getForum(), post.getThread(), TimestampHelper.toTimestamp(post.getCreated()));
            jdbcTemplate.update(subQuery, post.getForum());
            post.setId(id);
        }
        LOGGER.debug("create posts with user ");
    }

    public void create(Post post) {
        String SQL = "INSERT INTO post (parent_id, author, message, isEdited, forum, thread, created) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, post.getParent(), post.getAuthor(), post.getMessage(), post.getIsEdited(), post.getForum(), post.getThread(), TimestampHelper.toTimestamp(post.getCreated()));
        String sqlForum =
                "UPDATE forum SET posts = posts + 1 " +
                        "WHERE slug = ? ;";
        jdbcTemplate.update(sqlForum, post.getForum());
        LOGGER.debug("created" + post.getId() + " with user ");
    }

    public int getCount() {
        String SQL = "SELECT COUNT(*) FROM post";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("getVoiceWithNickname success");
        return count;
    }

    public List<Post> flatSort(int id, int limit, int marker, boolean desc) {
        String SQL = "SELECT * FROM post WHERE thread = ? ";

        if (desc) {
            SQL += "ORDER BY id DESC ";
        } else
            SQL += "ORDER BY id ";

        SQL += " LIMIT ? ";
        SQL += " OFFSET ? ";

        List<Post> posts = jdbcTemplate.query(SQL, new PostMapper(), id, limit, marker);
        LOGGER.debug("get posts success");
        return posts;
    }

    public List<Post> treeSort(int id, int limit, int marker, boolean desc) {
        String sql =
                "WITH RECURSIVE recursepost (id, parent_id, path, author, message, isEdited, forum, thread, created) AS ( " +
                "SELECT id, parent_id, array[id], author, message, isEdited, forum, thread, created FROM post WHERE parent_id = 0 " +
                "UNION ALL " +
                "SELECT p.id, p.parent_id, array_append(path, p.id), p.author, p.message, p.isEdited, p.forum, p.thread, p.created FROM post AS p " +
                "JOIN recursepost rp ON rp.id = p.parent_id ) " +
                "SELECT id, parent_id, path, author, message, isEdited, forum, thread, created FROM recursepost WHERE thread = ? ";
        if(desc) {
            sql += "ORDER BY path DESC ";
        } else
            sql += "ORDER BY path ";

        sql += "LIMIT ? ";
        sql += "OFFSET ? ;";
        List<Map<String, Object>> rows;
        final List<Post> posts = jdbcTemplate.query(sql, new PostMapper(), id, limit, marker);
        LOGGER.debug("get posts success");
        return posts;
    }

    public List<Post> getByThread(int id, Integer limit, String marker, String sort, Boolean desc) {
        List<Post> page = null;

        if (sort.toLowerCase().equals("flat")) {
            page = flatSort(id, limit, Integer.parseInt(marker), desc);
        }
        if (sort.toLowerCase().equals("tree")) {
            page = treeSort(id, limit, Integer.parseInt(marker), desc);
        }
//        if(sort.toLowerCase().equals("parent_tree")){
//            page = parentTreeSort(id, limit, Integer.parseInt(marker), desc);
//        }
        return page;
    }


    public Post getPostById(int id){
        final String SQL = "SELECT * FROM post WHERE id = ?";
        Post post = jdbcTemplate.queryForObject(SQL, new PostMapper(), id);
        LOGGER.debug("get post by id success");
        return post;
    }

    public void updatePost(String message, int id){
        String SQL = "UPDATE post SET message = ? WHERE id = ?";
        jdbcTemplate.update(SQL, message, id);
        LOGGER.debug("uodate post success");
    }
    public void delete() {
        final String SQL = "DELETE FROM post";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record");
    }
}
