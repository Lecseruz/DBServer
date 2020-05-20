package application.dao;

import application.models.Voice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by magomed on 26.04.17.
 */

@Repository
@Transactional
public class VoiceDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public VoiceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearTable() {
        final String query = "TRUNCATE TABLE voice CASCADE ";
        jdbcTemplate.execute(query);
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
