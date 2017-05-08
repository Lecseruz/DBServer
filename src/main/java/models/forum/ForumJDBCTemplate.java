package models.forum;

import models.user.User;
import models.user.UserMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ForumJDBCTemplate {

    private String marker = null;

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(ForumJDBCTemplate.class);

    @Autowired
    public ForumJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void createTable() {
        String sql =
                "CREATE TABLE IF NOT EXISTS forum ( " +
                        "title VARCHAR(128) NOT NULL, " +
                        "admin CITEXT NOT NULL, " +
                        "slug CITEXT UNIQUE NOT NULL, " +
                        "posts BIGINT NOT NULL DEFAULT 0, " +
                        "threads BIGINT NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (admin) REFERENCES m_user(nickname))";
        LOGGER.debug(sql + "create table success");

        jdbcTemplate.execute(sql);
    }


    public void create(String title, String admin, String slug, int posts, int thread) {
        String SQL = "INSERT INTO Forum (title, admin, slug, posts, threads) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, title, admin, slug, posts, thread);
        LOGGER.debug("created" + title + " with user " + admin);
        marker = slug;
    }

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS forum CASCADE ";
        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");

    }

    public List<Forum> listForum() {
        String SQL = "SELECT * FROM Forum";
        List<Forum> forums = jdbcTemplate.query(SQL, new ForumMapper());
        LOGGER.debug("get list forum success");
        return forums;
    }

    public Forum getForumBySlug(String slug) {
        String SQL = "SELECT * FROM Forum WHERE LOWER(slug) = LOWER(?)";
        Forum forum = jdbcTemplate.queryForObject(SQL, new ForumMapper(), slug);
        LOGGER.debug("get froum by slug success");
        return forum;
    }

    public List<User> getUsers(String slug, boolean desk, int limit, int id) {
        String SQL = "SELECT m.* FROM forum AS f JOIN m_user AS m ON f.admin = m.nickname " +
                    " WHERE f.threads > 0 OR f.posts > 0";
        if (id != 0){
            SQL += "OR m.id != " + id;
        }
        if (desk){
            SQL += " ORDER BY m.nickname desk ";
        }
        if (limit > 0){
            SQL +=" LIMIT ?";
        }
        List<User> users = jdbcTemplate.query(SQL, new UserMapper(), slug, limit);
        LOGGER.debug("getUsers success");
        return users;
    }

    public Forum getForumByNicknameAndTitle(String admin, String title) {
        String SQL = "SELECT * FROM forum WHERE LOWER(admin) = LOWER(?) AND LOWER(title) = LOWER(?)";
        Forum forum = jdbcTemplate.queryForObject(SQL, new ForumMapper(), admin, title);
        LOGGER.debug("get forum success");
        return forum;
    }

    public int getCount() {
        String SQL = "SELECT COUNT(*) FROM forum";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("get count success");
        return count;
    }


    public void delete() {
        String SQL = "DELETE FROM forum";
        jdbcTemplate.update(SQL);
        LOGGER.debug("Deleted Record");
    }

    public void update(String title, String name, String slug, int posts, int threads) {

    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }
}
