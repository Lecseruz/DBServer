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
    public void createTable() {
        String query = new StringBuilder()
                .append("CREATE EXTENSION IF NOT EXISTS citext; ")
                .append("CREATE TABLE IF NOT EXISTS m_user ( ")
                .append("nickname CITEXT UNIQUE NOT NULL PRIMARY KEY, ")
                .append("fullname varchar(128) NOT NULL, ")
                .append("abbout text NOT NULL, ")
                .append("email CITEXT UNIQUE NOT NULL); ")
                .toString();

        LOGGER.debug(query);
        jdbcTemplate.execute(query);
    }

    @Override
    public void dropTable() {
        String query = "DROP TABLE IF EXISTS m_user";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop success");

    }

    @Override
    public void create(String nickname, String fullname, String abbout, String email) {
        String SQL = "insert into m_user(nickname, fullname, abbout, email) values (?, ?, ?, ?)";
        jdbcTemplate.update(SQL, nickname, fullname, abbout, email);
        LOGGER.debug("insert success");
    }

    @Override
    public User getUserByNickname(String nickname) {
        String SQL = "select * from m_user where LOWER(nickname) = LOWER(?)";
        User users = jdbcTemplate.queryForObject(SQL,new UserMapper(), nickname);
        LOGGER.debug("getUserByNickname success");
        return users;
    }

    @Override
    public List<User> getUserByNicknameAndEmail(String nickname, String email) {
        final String SQL = "SELECT * FROM m_user WHERE LOWER(nickname) = LOWER(?) OR LOWER(email) = LOWER(?)";
        List<User> user = jdbcTemplate.query(SQL, new UserMapper(), nickname, email);
        if (user.isEmpty()) {
            return null;
        } else {
            LOGGER.debug("getUserByNicknameAndEmail success");
            return user;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String SQL = "select * from M_user where email = ?";
        User users = jdbcTemplate.queryForObject(SQL, new Object[] { email }, new UserMapper());
        LOGGER.debug("getUserByEmail success");
        return users;
    }

    @Override
    public void updateFullname(String fullname, String nickname) {
        String SQL = "update m_user set fullname = ? where LOWER(nickname) = LOWER(?)";
        jdbcTemplate.update(SQL, fullname, nickname);
        LOGGER.debug("Updated fullname" );
    }

    @Override
    public void updateAbbout(String about, String nickname) {
        String SQL = "update m_user set abbout = ? where LOWER(nickname) = LOWER(?)";
        jdbcTemplate.update(SQL, about, nickname);
        LOGGER.debug("Updated about" );
    }

    @Override
    public void updateEmail(String email, String nickname) {
        String SQL = "update m_user set email = ? where LOWER(nickname) = LOWER(?)";
        jdbcTemplate.update(SQL, email, nickname);
        LOGGER.debug("Updated email" );
    }

    @Override
    public void delete() {
        String SQL = "delete from m_user";
        jdbcTemplate.update(SQL);
        LOGGER.debug("Deleted success" );
    }

    @Override
    public int getCount() {
        String SQL = "select COUNT(*) from M_user";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
        LOGGER.debug("getCount success");
        return count;
    }

    @Override
    public List<User> listUsers() {
        String SQL = "select * from M_User";
        List<User> users = jdbcTemplate.query(SQL, new UserMapper());
        LOGGER.debug("getListUsers success");
        return users;
    }

    public void update(String nickname, String about, String fullname, String email) {
        String SQL = "update m_user set fullname = ?, abbout = ?, email = ? where nickname = ?";
        jdbcTemplate.update(SQL, fullname, about, email, nickname);
        LOGGER.debug("Updated" );
    }
}