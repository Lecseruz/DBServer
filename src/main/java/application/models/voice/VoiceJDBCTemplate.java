package application.models.voice;

import org.springframework.beans.factory.annotation.Autowired;
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
        final String query = "TRUNCATE TABLE voice CASCADE ";
        jdbcTemplate.execute(query);
//        LOGGER.debug("drop table success");
    }

    public int addVote(Voice vote) {
        final int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM voice WHERE user_id " +
                "IN (SELECT id FROM m_user WHERE LOWER(nickname) = LOWER(?)) AND thread_id = ?",
                Integer.class, vote.getAuthor(), vote.getThread());
        if (count == 0) {
            final String sql = "INSERT INTO voice (user_id, count, thread_id) VALUES (" +
                    "(SELECT id FROM m_user m WHERE LOWER(m.nickname) = LOWER(?)), ?, ?)";
            jdbcTemplate.update(sql, vote.getAuthor(), vote.getVoice(), vote.getThread());
        } else {
            final String sql = "UPDATE voice SET count = ? " +
                    "WHERE user_id IN (SELECT id FROM m_user WHERE LOWER(nickname) = LOWER(?)) AND thread_id = ?";
            jdbcTemplate.update(sql, vote.getVoice(), vote.getAuthor(), vote.getThread());
        }
        final String sql = "UPDATE thread  SET votes = (SELECT SUM(count) as votes FROM voice " +
                "WHERE (thread_id) = ?) WHERE id = ? RETURNING votes";
        return jdbcTemplate.queryForObject(sql , Integer.class, vote.getThread(), vote.getThread());
    }
}
