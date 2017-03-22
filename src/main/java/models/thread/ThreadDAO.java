package models.thread;

/**
 * Created by magomed on 18.03.17.
 */
import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;
import javax.sql.DataSource;

interface ThreadDAO {
    public void createTable();

    public void  dropTable();
    public void create(Integer id, String title, String author, String forum, String message, Integer votes, String slug, Timestamp created);

    /**
     * This is the method to be used to list down
     * a record from the Student table corresponding
     * to a passed student id.
     */
    /**
     * This is the method to be used to list down
     * all the records from the Student table.
     */
    public Thread getThread(Integer id);
    /**
     * This is the method to be used to delete
     * a record from the Student table corresponding
     * to a passed student id.
     */
    public void delete();

    public int getCount();

    /**
     * This is the method to be used to update
     * a record into the Student table.
     */
}
