package models.user;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserJDBCTemplate implements UserDAO {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = Logger.getLogger(UserJDBCTemplate.class);

    @Autowired
    public UserJDBCTemplate(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(String nickname, String fullname, String abbout, String email) {
        String SQL = "insert into m_user(nickname, fullname, abbout, email) values (?, ?, ?, ?)";
        jdbcTemplate.update(SQL, nickname, fullname, abbout, email);
        LOGGER.debug("да пошёл он на хуй");
    }

    @Override
    public User getUserByNickname(String nickname) {
        String SQL = "select * from M_user where nickname = ?";
        User users = jdbcTemplate.queryForObject(SQL, new Object[] { nickname }, new UserMapper());
        return users;
    }

    @Override
    public User getUserByNicknameAndEmail(String nickname, String email) {
        String SQL = "select * from M_user where nickname = ? OR email = ?";
        User user = jdbcTemplate.queryForObject(SQL,new Object[] { nickname, email }, new UserMapper());
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        String SQL = "select * from M_user where email = ?";
        User users = jdbcTemplate.queryForObject(SQL, new Object[] { email }, new UserMapper());
        return users;
    }


    @Override
    public void delete() {
        String SQL = "delete from m_user";
        jdbcTemplate.update(SQL);
        System.out.println("Deleted Record" );
    }

    @Override
    public int getCount() {
        String SQL = "select COUNT(*) from M_user";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        return count;
    }

    @Override
    public List<User> listUsers() {
        String SQL = "select * from M_User";
        List<User> users = jdbcTemplate.query(SQL, new UserMapper());
        return users;
    }

    public void update(String nickname, String about, String fullname, String email) {
        String SQL = "update m_user set fullname = ?, abbout = ?, email = ? where nickname = ?";
        jdbcTemplate.update(SQL, fullname, about, email, nickname);
        System.out.println("Updated" );
    }
}