package application.dao;

import java.util.List;

import application.dao.mapper.UserMapper;
import application.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearTable() {
        final String sql = "TRUNCATE TABLE m_user CASCADE ";

        jdbcTemplate.execute(sql);
    }

    public void create(User user) {
        final String sql = "INSERT INTO m_user(nickname, fullname, abbout, email) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getNickname(), user.getFullname(), user.getAbout(), user.getEmail());
    }

    public User getUserByNickname(String nickname) {
        try {
            final String sql = "SELECT * FROM m_user WHERE LOWER (nickname COLLATE \"ucs_basic\") = LOWER(? COLLATE \"ucs_basic\")";
            return jdbcTemplate.queryForObject(sql, new UserMapper(), nickname);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<User> getUsersByNicknameOrEmail(String nickname, String email) {
        try {
            final String sql = "SELECT * FROM m_user WHERE LOWER(nickname COLLATE \"ucs_basic\") = LOWER(? COLLATE \"ucs_basic\") OR LOWER(email) = LOWER(?)";
            return jdbcTemplate.query(sql, new UserMapper(), nickname, email);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<User> getByForum(int id, Integer limit, String since, boolean desc) {
        String sql =
                "SELECT * FROM m_user WHERE id IN (" +
                        " SELECT user_id FROM m_users_forums WHERE forum_id = ?) "; // TODO : bad programmist

        if (since != null) {
            if (desc) {
                sql += "AND LOWER(nickname COLLATE \"ucs_basic\") < LOWER('" + since + "' COLLATE \"ucs_basic\") ";
            } else
                sql += "AND LOWER(nickname COLLATE \"ucs_basic\") > LOWER('" + since + "' COLLATE \"ucs_basic\") ";
        }

        if (desc) {
            sql += "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") DESC ";
        } else
            sql += "ORDER BY LOWER(nickname COLLATE \"ucs_basic\") ";

        if (limit != 0) {
            sql += "LIMIT " + limit + " ;";
        }
        return jdbcTemplate.query(sql, new UserMapper(), id);
    }

    public User update(User user) {
        final String sql = "UPDATE m_user SET " +
                "abbout = COALESCE (?, abbout), " +
                "email = COALESCE (?, email), " +
                "fullname = COALESCE (?, fullname)" +
                "WHERE LOWER (nickname COLLATE \"ucs_basic\") = LOWER (? COLLATE \"ucs_basic\")";
        final int rows = jdbcTemplate.update(sql, user.getAbout(), user.getEmail(), user.getFullname(), user.getNickname());
        if (rows == 0) {
            return null;
        }
        return getUserByNickname(user.getNickname());
    }

    public int getCount() {
        final String sql = "SELECT COUNT(*) FROM M_user";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}