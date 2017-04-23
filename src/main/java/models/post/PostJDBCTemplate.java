package models.post;

import models.thread.ThreadJDBCTemplate;
import models.thread.ThreadMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class PostJDBCTemplate implements PostDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    @Autowired
    public PostJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createTable(){
        String sql = new StringBuilder()
                .append("CREATE EXTENSION IF NOT EXISTS citext; ")
                .append("CREATE TABLE IF NOT EXISTS post ( ")
                .append("id SERIAL PRIMARY KEY, ")
                .append("parent_id BIGINT NOT NULL DEFAULT 0, ")
                .append("author CITEXT NOT NULL, ")
                .append("message TEXT NOT NULL, ")
                .append("isEdited BOOLEAN NOT NULL DEFAULT false, ")
                .append("forum CITEXT NOT NULL, ")
                .append("thread BIGINT NOT NULL, ")
                .append("created TIMESTAMP NOT NULL DEFAULT current_timestamp);")
                .toString();
        LOGGER.debug(sql +
                "create table success");

        jdbcTemplate.execute(sql);
    }

    @Override
    public void dropTable(){
        String query = "DROP TABLE IF EXISTS post";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    public void createPosts(List<Post> posts) {
        String SQL = "insert into post (parent_id, author, message, isEdited, forum, thread, created) values (?, ?, ?, ?, ?, ?, ?)";
        for (Post post : posts) {
            jdbcTemplate.update(SQL, post.getParentId(), post.getAuthor(), post.getMessage(), post.getIsEdited(), post.getForum(), post.getThread(), ThreadMapper.toTimestamp(post.getCreated()));
        }
        LOGGER.debug("create posts with user ");
    }

    public void create(Post post) {
        String SQL = "insert into post (parent_id, author, message, isEdited, forum, thread, created) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, post.getParentId(), post.getAuthor(), post.getMessage(), post.getIsEdited(), post.getForum(), post.getThread(), ThreadMapper.toTimestamp(post.getCreated()));
        LOGGER.debug("created" + post.getId() + " with user ");
    }

    public int getCount(){
        String SQL = "select COUNT(*) from post";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("getCount success");
        return count;
    }

    @Override
    public void delete() {
        String SQL = "delete from post";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }
}
