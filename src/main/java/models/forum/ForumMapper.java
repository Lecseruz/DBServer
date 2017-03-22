package models.forum;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
/**
 * Created by magomed on 12.03.17.
 */
public class ForumMapper implements RowMapper<Forum> {
    public Forum mapRow(ResultSet rs, int rowNum) throws SQLException {
        Forum forum = new Forum();
        forum.setTitle(rs.getString("title"));
        forum.setSlag(rs.getString("slug"));
        forum.setThread(rs.getInt("threads"));
        forum.setPosts(rs.getInt("posts"));
        forum.setNickname(rs.getString("nickname"));

        return forum;
    }
}
