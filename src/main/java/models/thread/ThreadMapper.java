package models.thread;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import static config.TimestampHelper.fromTimestamp;

/**
 * Created by magomed on 19.03.17.
 */
public class ThreadMapper implements RowMapper<Thread> {

    @Override
    public Thread mapRow(ResultSet resultSet, int i) throws SQLException {
        String author = resultSet.getString("author");
        String title = resultSet.getString("title");
        String created = fromTimestamp(resultSet.getTimestamp("created"));
        String forum = resultSet.getString("forum");
        int id = resultSet.getInt("id");
        String message = resultSet.getString("message");
        String slug = resultSet.getString("slug");
        int votes = resultSet.getInt("votes");
        return new Thread(id, title, author, forum, message, votes, slug, created);
    }
}
