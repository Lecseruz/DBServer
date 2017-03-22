package models.forum;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ForumJDBCTemplate implements ForumDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ForumJDBCTemplate.class);

    @Autowired
    public ForumJDBCTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(String title, String f_user, String slug, int posts, int thread) {
        String SQL = "insert into Forum (title, nickname, slug, posts, threads) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, title, f_user, slug, posts, thread);
        LOGGER.debug("created" + title + " with user " +  f_user);
    }

    @Override
    public List<Forum> listForum() {
        String SQL = "select * from Forum";
        List <Forum> forums = jdbcTemplate.query(SQL, new ForumMapper());
        return forums;
    }

    @Override
    public Forum getForumWithSlug(String slug) {
        String SQL = "select * from Forum where slug = ?";
        List <Forum> forums = jdbcTemplate.query(SQL, new ForumMapper(), slug);
        if (!forums.isEmpty()){
            return forums.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Forum getForum(String nickname, String title ) {
        String SQL = "select * from Forum where nickname = ? and title = ?";
        List <Forum> forums = jdbcTemplate.query(SQL, new ForumMapper(), nickname, title);
        if (!forums.isEmpty()){
            return forums.get(0);
        } else {
            return null;
        }
    }



    @Override
    public void delete() {
        String SQL = "delete from forum";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }

    @Override
    public void update(String title, String name, String slug, int posts, int threads) {

    }
}
