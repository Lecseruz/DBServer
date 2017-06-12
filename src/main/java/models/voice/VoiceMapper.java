package models.voice;

import models.thread.Thread;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by magomed on 26.04.17.
 */
public class VoiceMapper implements RowMapper<Voice> {
    @Override
    public Voice mapRow(ResultSet resultSet, int i) throws SQLException {
        Voice voice = new Voice();
        voice.setNickname(resultSet.getString("author"));
        voice.setVoice(resultSet.getInt("count"));
        voice.setThread_id(resultSet.getInt("thread_id"));
        return voice;
    }
}
