package models.thread;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by magomed on 19.03.17.
 */
public class ThreadMapper implements RowMapper<Thread> {

    @Override
    public Thread mapRow(ResultSet resultSet, int i) throws SQLException {
        Thread thread = new Thread();
        thread.setAuthor(resultSet.getString("author"));
        thread.setTitle(resultSet.getString("title"));
        thread.setCreated(resultSet.getTimestamp("created"));
        thread.setForum(resultSet.getString("forum"));
        thread.setId(resultSet.getInt("id"));
        thread.setMessage(resultSet.getString("message"));
        thread.setSlug(resultSet.getString("slug"));
        thread.setVotes(resultSet.getInt("votes"));
        return thread;
    }
}
