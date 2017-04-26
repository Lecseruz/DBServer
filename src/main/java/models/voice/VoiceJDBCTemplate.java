package models.voice;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by magomed on 26.04.17.
 */

@Service
public class VoiceJDBCTemplate {
    private JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = Logger.getLogger(VoiceJDBCTemplate.class);

    @Autowired
    public VoiceJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTable() {
        String query = new StringBuilder()
                .append("CREATE EXTENSION IF NOT EXISTS citext; ")
                .append("CREATE TABLE IF NOT EXISTS voice ( ")
                .append("id SERIAL PRIMARY KEY, ")
                .append("nickname CITEXT UNIQUE NOT NULL, ")
                .append("count int NOT NULL); ")
                .toString();
        LOGGER.debug(query + "success");

        jdbcTemplate.execute(query);
    }

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS voice";

        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");
    }
    public int createVoice(Voice voice){
        String SQL = "insert into voice (nickname, count) values(?,?) returning id";
        int id = jdbcTemplate.queryForObject(SQL, Integer.class, voice.getNickname(), voice.getVoice());
        LOGGER.debug("create succes");
        return id;
    }

    public Voice getVoiceWithNickname(String nickname){
        String SQL = "select * from voice where LOWER(nickname) = LOWER(?)";
        Voice voice = jdbcTemplate.queryForObject(SQL, new VoiceMapper(), nickname);
        LOGGER.debug("succes");
        return voice;
    }

    public int updateVoice(int count, String nickname){
        String SQL = "update voice set count = ? WHERE LOWER(nickname) = LOWER(?) returning id";
        int id  = jdbcTemplate.queryForObject(SQL, Integer.class, count, nickname);
        return id;
    }
}
