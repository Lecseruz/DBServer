package models.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper<User> {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setAbout(rs.getString("abbout"));
        user.setNickname(rs.getString("nickname"));
        user.setFullname(rs.getString("fullname"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}