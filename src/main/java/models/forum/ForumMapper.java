package models.forum;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
/**
 * Created by magomed on 12.03.17.
 */
public class ForumMapper implements RowMapper<Forum> {
    public Forum mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Forum forum = new Forum();
        forum.setTitle(rs.getString("title"));
        forum.setSlug(rs.getString("slug"));
        forum.setThreads(rs.getInt("threads"));
        forum.setPosts(rs.getInt("posts"));
        forum.setUser(rs.getString("admin"));

        return forum;
    }
}
