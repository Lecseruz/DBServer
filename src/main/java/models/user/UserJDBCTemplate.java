package models.user;


import java.util.List;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserJDBCTemplate implements UserDAO {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = Logger.getLogger(UserJDBCTemplate.class);

    @Autowired
    public UserJDBCTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(String nickname, String fullname, String abbout, String email) {
        String SQL = "insert into m_user(nickname, fullname, abbout, email) values (?, ?, ?, ?)";
        jdbcTemplate.update(SQL, nickname, fullname, abbout, email);
        LOGGER.debug("да пошёл он на хуй");
    }

    @Override
    public User getUser(String nickname) {
        String SQL = "select * from M_user where nickname = ?";
        List<User> users = jdbcTemplate.query(SQL, new UserMapper(), nickname);
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            return null;
        }
    }

    @Override
    public User getUserWithEmail(String nickname, String email) {
        String SQL = "select * from M_user where nickname = ? OR email = ?";
        List<User> users = jdbcTemplate.query(SQL, new UserMapper(), nickname, email);
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            return null;
        }    }

    @Override
    public void delete() {
        String SQL = "delete from m_user";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }

    @Override
    public List<User> listUsers() {
        String SQL = "select * from M_User";
        List<User> users = jdbcTemplate.query(SQL, new UserMapper());
        return users;
    }

    public void update(Integer id, Integer age) {
    }
}