package application.models.user;


import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserJDBCTemplate {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserJDBCTemplate(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearTable() {
        final String sql = "TRUNCATE TABLE m_user CASCADE ";

        jdbcTemplate.execute(sql);
    }

    public void create(String nickname, String fullname, String abbout, String email) {
        final String sql = "INSERT INTO m_user(nickname, fullname, abbout, email) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, nickname, fullname, abbout, email);
    }

    public @Nullable User getUserByNickname(String nickname) {
        try {
            final String sql = "SELECT * FROM m_user WHERE LOWER (nickname COLLATE \"ucs_basic\") = LOWER(? COLLATE \"ucs_basic\")";
            return jdbcTemplate.queryForObject(sql, new UserMapper(), nickname);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public @Nullable List<User> getUsersByNicknameOrEmail(String nickname, String email) {
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

    public @Nullable User update(String about, String email, String fullname, String nickname) {
        final String sql = "UPDATE m_user SET " +
                "abbout = COALESCE (?, abbout), " +
                "email = COALESCE (?, email), " +
                "fullname = COALESCE (?, fullname)" +
                "WHERE LOWER (nickname COLLATE \"ucs_basic\") = LOWER (? COLLATE \"ucs_basic\")";
        final int rows = jdbcTemplate.update(sql, about, email, fullname, nickname);
        if (rows == 0) {
            return null;
        }
        return getUserByNickname(nickname);
    }

    public int getCount() {
        final String sql = "SELECT COUNT(*) FROM M_user";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}