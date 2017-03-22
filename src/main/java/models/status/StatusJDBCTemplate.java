package models.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by magomed on 20.03.17.
 */
public class StatusJDBCTemplate implements StatusDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatusJDBCTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void updateCountForum(Integer integer) {
        String SQL = "update status set count_forum = ?";
        jdbcTemplate.update(SQL, integer);
        System.out.println("Updated Record with ID = " + integer );
    }

    @Override
    public void updateCountUser(Integer integer) {

    }

    @Override
    public void updateCountThread(Integer integer) {
        String SQL = "update status set count_thread = ?";
        jdbcTemplate.update(SQL, integer);
        System.out.println("Updated Record with ID = " + integer );
    }

    @Override
    public void updateCountPosts(Integer integer) {

    }

    @Override
    public void delete() {
        String SQL = "delete from Status";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }
}
