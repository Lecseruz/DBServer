package application.models.thread;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import static application.config.TimestampHelper.fromTimestamp;

/**
 * Created by magomed on 19.03.17.
 */
public class ThreadMapper implements RowMapper<Thread> {

    @Override
    public Thread mapRow(ResultSet resultSet, int i) throws SQLException {
        final String author = resultSet.getString("__nickname");
        final String title = resultSet.getString("title");
        final String created = fromTimestamp(resultSet.getTimestamp("created"));
        final String forum = resultSet.getString("forum_slug");
        final int id = resultSet.getInt("id");
        final String message = resultSet.getString("message");
        final String slug = resultSet.getString("slug");
        final int votes = resultSet.getInt("votes");
        return new Thread(id, title, author, forum, message, votes, slug, created);
    }
}
