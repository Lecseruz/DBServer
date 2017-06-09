package models.user;


import java.util.List;

import models.post.Post;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserJDBCTemplate {

    private final JdbcTemplate jdbcTemplate;
//    private static final Logger LOGGER = Logger.getLogger(UserJDBCTemplate.class);

    @Autowired
    public UserJDBCTemplate(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTable() {
        String query =
                "CREATE EXTENSION IF NOT EXISTS citext; " +
                        "CREATE TABLE IF NOT EXISTS m_user ( " +
                        "id SERIAL NOT NULL PRIMARY KEY," +
                        "nickname CITEXT UNIQUE NOT NULL, " +
                        "fullname VARCHAR(128) NOT NULL, " +
                        "abbout TEXT NOT NULL, " +
                        "email CITEXT UNIQUE NOT NULL); ";
//        LOGGER.debug(query);
        jdbcTemplate.execute(query);
    }

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS m_user";

        jdbcTemplate.execute(query);
//        LOGGER.debug("drop success");

    }

    public void create(String nickname, String fullname, String abbout, String email) {
        String SQL = "INSERT INTO m_user(nickname, fullname, abbout, email) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(SQL, nickname, fullname, abbout, email);
//        LOGGER.debug("insert success");
    }

    public User getUserByNickname(String nickname) {
        try {
            final String SQL = "SELECT * FROM m_user WHERE LOWER(nickname) = LOWER(?)";
            final User users = jdbcTemplate.queryForObject(SQL, new UserMapper(), nickname);
//            LOGGER.debug("getUserByNickname success");
            return users;
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<User> getUserByNicknameAndEmail(String nickname, String email) {
        final String SQL = "SELECT * FROM m_user WHERE LOWER(nickname) = LOWER(?) OR LOWER(email) = LOWER(?)";
        List<User> user = jdbcTemplate.query(SQL, new UserMapper(), nickname, email);
        if (user.isEmpty()) {
            return null;
        } else {
//            LOGGER.debug("getUserByNicknameAndEmail success");
            return user;
        }
    }

    public List<User> getByForum(String slug, Integer limit, String since, boolean desc) {
        String SQL =
                "SELECT * FROM m_user WHERE nickname IN (" +
                        "SELECT author FROM post WHERE forum = '" + slug + "' UNION " +
                        "SELECT author FROM thread WHERE forum = '" + slug + "' ) "; // TODO : bad programmist

        if (since != null) {
            if (desc) {
                SQL += "AND LOWER(nickname COLLATE \"ucs_basic\") < LOWER('" + since + "' COLLATE \"ucs_basic\") ";
            } else
                SQL += "AND LOWER(nickname COLLATE \"ucs_basic\") > LOWER('" + since + "' COLLATE \"ucs_basic\") ";
        }

        if (desc) {
            SQL += "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") DESC ";
        } else
            SQL += "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") ";

        if (limit != 0) {
            SQL += "LIMIT " + limit + " ;";
        }
        List<User> users = jdbcTemplate.query(SQL, new UserMapper());
//        LOGGER.debug("getUsers success");
        return users;
    }


    public User getUserByEmail(String email) {
        String SQL = "SELECT * FROM M_user WHERE email = ?";
        User users = jdbcTemplate.queryForObject(SQL, new Object[]{email}, new UserMapper());
//        LOGGER.debug("getUserByEmail success");
        return users;
    }

    public void updateFullname(String fullname, String nickname) {
        String SQL = "UPDATE m_user SET fullname = ? WHERE LOWER(nickname) = LOWER(?)";
        jdbcTemplate.update(SQL, fullname, nickname);
//        LOGGER.debug("Updated fullname");
    }

    public void updateAbbout(String about, String nickname) {
        String SQL = "UPDATE m_user SET abbout = ? WHERE LOWER(nickname) = LOWER(?)";
        jdbcTemplate.update(SQL, about, nickname);
//        LOGGER.debug("Updated about");
    }

    public void updateEmail(String email, String nickname) {
        String SQL = "UPDATE m_user SET email = ? WHERE LOWER(nickname) = LOWER(?)";
        jdbcTemplate.update(SQL, email, nickname);
//        LOGGER.debug("Updated email");
    }

    public void delete() {
        String SQL = "DELETE FROM m_user";
        jdbcTemplate.update(SQL);
//        LOGGER.debug("Deleted success");
    }

    public int getCount() {
        String SQL = "SELECT COUNT(*) FROM M_user";
        int count = jdbcTemplate.queryForObject(SQL, Integer.class);
//        LOGGER.debug("getVoiceWithNickname success");
        return count;
    }

    public List<User> listUsers() {
        String SQL = "SELECT * FROM M_User";
        List<User> users = jdbcTemplate.query(SQL, new UserMapper());
//        LOGGER.debug("getListUsers success");
        return users;
    }

    public void update(String nickname, String about, String fullname, String email) {
        String SQL = "UPDATE m_user SET fullname = ?, abbout = ?, email = ? WHERE nickname = ?";
        jdbcTemplate.update(SQL, fullname, about, email, nickname);
//        LOGGER.debug("Updated");
    }
}