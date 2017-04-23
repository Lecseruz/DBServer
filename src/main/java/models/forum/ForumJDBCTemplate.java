package models.forum;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ForumJDBCTemplate {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ForumJDBCTemplate.class);

    @Autowired
    public ForumJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


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


    public void create(String title, String admin, String slug, int posts, int thread) {
        String SQL = "insert into Forum (title, admin, slug, posts, threads) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, title, admin, slug, posts, thread);
        LOGGER.debug("created" + title + " with user " +  admin);
    }

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS forum";
        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    public List<Forum> listForum() {
        String SQL = "select * from Forum";
        List <Forum> forums = jdbcTemplate.query(SQL, new ForumMapper());
        LOGGER.debug("get list forum success");
        return forums;
    }

    public Forum getForumBySlug(String slug) {
        String SQL = "select * from Forum where LOWER(slug) = LOWER(?)";
        Forum forum = jdbcTemplate.queryForObject(SQL, new ForumMapper(), slug);
        LOGGER.debug("get froum by slug success");

        return forum;
    }

    public Forum getForumByNicknameAndTitle(String admin, String title ) {
        String SQL = "select * from forum where LOWER(admin) = LOWER(?) and LOWER(title) = LOWER(?)";
        Forum forum = jdbcTemplate.queryForObject(SQL,  new ForumMapper(), admin, title);
        LOGGER.debug("get forum success");
        return forum;
    }

    public int getCount() {
        String SQL = "select COUNT(*) from forum";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("get count success");
        return count;
    }


    public void delete() {
        String SQL = "delete from forum";
        jdbcTemplate.update(SQL);
        LOGGER.debug("Deleted Record" );
    }

    public void update(String title, String name, String slug, int posts, int threads) {

    }
}
