package models.forum;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ForumJDBCTemplate implements ForumDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ForumJDBCTemplate.class);

    @Autowired
    public ForumJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public Forum getForumBySlug(String slug) {
        String SQL = "select * from Forum where slug = ?";
        Forum forum = jdbcTemplate.queryForObject(SQL, new Object[] { slug }, new ForumMapper());
        return forum;
    }

    @Override
    public Forum getForum(String nickname, String title ) {
        String SQL = "select * from Forum where nickname = ? and title = ?";
        Forum forum = jdbcTemplate.queryForObject(SQL, new Object[] { nickname, title }, new ForumMapper());
        return forum;
    }

    @Override
    public int getCount() {
        String SQL = "select COUNT(*) from forum";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        return count;
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
