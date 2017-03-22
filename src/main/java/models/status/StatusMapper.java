package models.status;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by magomed on 20.03.17.
 */
public class StatusMapper implements RowMapper<Status> {

    @Override
    public Status mapRow(ResultSet resultSet, int i) throws SQLException {
        Status status = new Status();
        status.setCount_forum(resultSet.getInt("count_forum"));
        status.setCount_user(resultSet.getInt("count_user"));
        status.setCount_post(resultSet.getInt("count_post"));
        status.setCount_thread(resultSet.getInt("count_thread"));
        return status;
    }
}