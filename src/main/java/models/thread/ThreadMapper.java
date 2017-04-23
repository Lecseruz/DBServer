package models.thread;

import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by magomed on 19.03.17.
 */
public class ThreadMapper implements RowMapper<Thread> {

    public static Timestamp toTimestamp(String line){
        String st = ZonedDateTime.parse(line).format(DateTimeFormatter.ISO_INSTANT);
        return  new Timestamp(ZonedDateTime.parse(st).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public static String fromTimestamp(Timestamp timestamp){
        return timestamp.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
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
