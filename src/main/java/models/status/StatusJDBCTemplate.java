package models.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by magomed on 20.03.17.
 */

@Service
@Transactional
public class StatusJDBCTemplate implements StatusDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatusJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateCountForum(Integer integer) {
        String SQL = "update status set count_forum = ?";
        jdbcTemplate.update(SQL, integer);
        System.out.println("Updated Record with ID = " + integer );
    }

    @Override
    public void updateCountUser(Integer integer) {
        String SQL = "update status set count_user = ?";
        jdbcTemplate.update(SQL, integer);
        System.out.println("Updated Record with ID = " + integer );
    }

    @Override
    public void updateCountThread(Integer integer) {
        String SQL = "update status set count_thread = ?";
        jdbcTemplate.update(SQL, integer);
        System.out.println("Updated Record with ID = " + integer );
    }

    @Override
    public void updateCountPosts(Integer integer) {
        String SQL = "update status set count_posts = ?";
        jdbcTemplate.update(SQL, integer);
        System.out.println("Updated Record with ID = " + integer );
    }

    @Override
    public int getCountForum() {
        String SQL = "select count_forum from status";
        Integer count = jdbcTemplate.queryForObject(SQL, Integer.class);
        return count;
    }

    @Override
    public int getCountPost() {
        String SQL = "select count_post from status";
        Integer count = jdbcTemplate.queryForObject(SQL, Integer.class);
        return count;
    }

    @Override
    public int getCountUser() {
        String SQL = "select count_user from status";
        Integer count = jdbcTemplate.queryForObject(SQL, Integer.class);
        return count;
    }

    @Override
    public int getCountThread() {
        String SQL = "select count_thread from status";
        Integer count = jdbcTemplate.queryForObject(SQL, Integer.class);
        return count;
    }

    @Override
    public void delete() {
        String SQL = "delete from Status";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }
}
