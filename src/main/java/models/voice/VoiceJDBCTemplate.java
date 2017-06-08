package models.voice;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
        String query =
                "CREATE EXTENSION IF NOT EXISTS citext; " +
                        "CREATE TABLE IF NOT EXISTS voice ( " +
                        "id SERIAL PRIMARY KEY, " +
                        "nickname CITEXT NOT NULL, " +
                        "count int NOT NULL," +
                        "thread_id int NOT NULL); ";
        LOGGER.debug(query + "success");

        jdbcTemplate.execute(query);
    }

    public void dropTable() {
        String query = "DROP TABLE IF EXISTS voice";
        jdbcTemplate.execute(query);
        LOGGER.debug("drop table success");
    }

    public int createVoice(Voice voice) {
        String SQL = "INSERT INTO voice (nickname, count, thread_id) VALUES(?,?,?) RETURNING id";
        int id = jdbcTemplate.queryForObject(SQL, Integer.class, voice.getNickname(), voice.getVoice(), voice.getThread_id());
        LOGGER.debug("create succes");
        return id;
    }

    public Voice getVoiceWithNicknameAndThread(String nickname, int thread_id) {
        try {
            String SQL = "SELECT * FROM voice WHERE LOWER(nickname) = LOWER(?) AND thread_id = ?";
            Voice voice = jdbcTemplate.queryForObject(SQL, new VoiceMapper(), nickname, thread_id);
            LOGGER.debug("succes");
            return voice;
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void updateVoice(Voice voice) {
        String SQL = "UPDATE voice SET count = ? WHERE LOWER(nickname) = LOWER(?) and thread_id = ?";
        jdbcTemplate.update(SQL, voice.getVoice(), voice.getNickname(), voice.getThread_id());
        LOGGER.debug("update success");
    }
}
