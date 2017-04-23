package models.post;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by magomed on 19.03.17.
 */
public interface PostDAO {
    /**
     * This is the method to be used to create
     * a record in the Student table.
     */
    public void createTable();

    public void  dropTable();

    /**
     * This is the method to be used to delete
     * a record from the Student table corresponding
     * to a passed student id.
     */
    public void delete();

}
