package application.dao.mapper;

import application.config.TimestampHelper;
import application.models.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by magomed on 19.03.17.
 */
public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet resultSet, int i) throws SQLException {
        final Post post = new Post();
        post.setAuthor(resultSet.getString("__nickname"));
        post.setCreated(TimestampHelper.fromTimestamp(resultSet.getTimestamp("created")));
        post.setForum(resultSet.getString("forum_slug"));
        post.setId(resultSet.getInt("id"));
        post.setMessage(resultSet.getString("message"));
        post.setParent(resultSet.getInt("parent_id"));
        post.setThread(resultSet.getInt("thread"));
        post.setIsEdited(resultSet.getBoolean("isEdited"));
        return post;
    }
}
