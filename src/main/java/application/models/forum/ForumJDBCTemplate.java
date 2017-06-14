package application.models.forum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ForumJDBCTemplate {

    private final JdbcTemplate jdbcTemplate;

//    private static final Logger LOGGER = Logger.getLogger(ForumJDBCTemplate.class);

    @Autowired
    public ForumJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(String title, String admin, String slug) {
        final String sql = "INSERT INTO forum (title, slug, user_id) VALUES (?, ?, (select id from m_user WHERE LOWER(nickname) = LOWER(?)))";
        jdbcTemplate.update(sql, title, slug, admin);
//        LOGGER.debug("created" + title + " with user " + admin);
    }

    public void clearTable() {
        final String sql = "TRUNCATE TABLE forum CASCADE ";
        jdbcTemplate.execute(sql);
//        LOGGER.debug("drop table success");

    }

    public Forum getForumBySlug(String slug) {
        final String sql = "SELECT * FROM Forum f JOIN m_user m ON f.user_id = m.id WHERE LOWER(f.slug) = LOWER(?)";
        //        LOGGER.debug("get froum by slug success");
        return jdbcTemplate.queryForObject(sql, new ForumMapper(), slug);
    }

    public int getCount() {
        final String sql = "SELECT COUNT(*) FROM forum";
        //        LOGGER.debug("get count success");
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
