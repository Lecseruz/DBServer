package models.voice;

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

//    private static final Logger LOGGER = Logger.getLogger(VoiceJDBCTemplate.class);

    @Autowired
    public VoiceJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearTable() {
        String query = "TRUNCATE TABLE voice CASCADE ";
        jdbcTemplate.execute(query);
//        LOGGER.debug("drop table success");
    }

    public int createVoice(Voice voice) {
        final String sql = "INSERT INTO voice (author, count, thread_id) VALUES(?,?,?) RETURNING id";
        final int id = jdbcTemplate.queryForObject(sql, Integer.class, voice.getAuthor(), voice.getVoice(), voice.getThread_id());
//        LOGGER.debug("create succes");
        return id;
    }

    public Voice getVoiceWithNicknameAndThread(String nickname, int thread_id) {
        try {
            final String sql = "SELECT * FROM voice WHERE LOWER(author) = LOWER(?) AND thread_id = ?";
            final Voice voice = jdbcTemplate.queryForObject(sql, new VoiceMapper(), nickname, thread_id);
//            LOGGER.debug("succes");
            return voice;
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void updateVoice(Voice voice) {
        String SQL = "UPDATE voice SET count = ? WHERE LOWER(author) = LOWER(?) and thread_id = ?";
        jdbcTemplate.update(SQL, voice.getVoice(), voice.getAuthor(), voice.getThread_id());
//        LOGGER.debug("update success");
    }
}
