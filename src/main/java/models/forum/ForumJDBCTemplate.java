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
    public void createTable() {
        String sql = new StringBuilder()
                .append("CREATE EXTENSION IF NOT EXISTS citext; ")
                .append("CREATE TABLE IF NOT EXISTS forum ( ")
                .append("title VARCHAR(128) NOT NULL, ")
                .append("admin CITEXT NOT NULL, ")
                .append("slug CITEXT UNIQUE NOT NULL PRIMARY KEY, ")
                .append("posts BIGINT NOT NULL DEFAULT 0, ")
                .append("threads BIGINT NOT NULL DEFAULT 0, ")
                .append("FOREIGN KEY (admin) REFERENCES m_user(nickname)); ")
                .toString();
        LOGGER.debug(sql + "create table success");

        jdbcTemplate.execute(sql);
    }


    @Override
    public void create(String title, String f_user, String slug, int posts, int thread) {
        String SQL = "insert into Forum (title, nickname, slug, posts, threads) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, title, f_user, slug, posts, thread);
        LOGGER.debug("created" + title + " with user " +  f_user);
    }

    @Override
    public void dropTable() {
        String query = "DROP TABLE IF EXISTS forum";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    @Override
    public List<Forum> listForum() {
        String SQL = "select * from Forum";
        List <Forum> forums = jdbcTemplate.query(SQL, new ForumMapper());
        LOGGER.debug("get list forum success");
        return forums;
    }

    @Override
    public Forum getForumBySlug(String slug) {
        String SQL = "select * from Forum where slug = ?";
        Forum forum = jdbcTemplate.queryForObject(SQL, new Object[] { slug }, new ForumMapper());
        LOGGER.debug("get froum by slug success");

        return forum;
    }

    @Override
    public Forum getForum(String nickname, String title ) {
        String SQL = "select * from Forum where nickname = ? and title = ?";
        Forum forum = jdbcTemplate.queryForObject(SQL, new Object[] { nickname, title }, new ForumMapper());
        LOGGER.debug("get forum success");
        return forum;
    }

    @Override
    public int getCount() {
        String SQL = "select COUNT(*) from forum";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("get count success");
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
