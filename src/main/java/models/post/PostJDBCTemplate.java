package models.post;

import models.thread.ThreadJDBCTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

public class PostJDBCTemplate implements PostDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ThreadJDBCTemplate.class);

    @Autowired
    public PostJDBCTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public void create(Integer id, int parent, String author, String message, boolean isEdited, String forum, int thread, Timestamp created) {
        String SQL = "insert into post (id, parent_id, author, message, isEdited, forum, thread, created) values (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, id, parent, author, message, isEdited, forum, thread, created);
        LOGGER.debug("created" + id + " with user ");
    }

    @Override
    public List<Post> listThread() {
        return null;
    }

    @Override
    public void delete() {
        String SQL = "delete from post";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }

    @Override
    public void update(Integer id) {

    }

}
