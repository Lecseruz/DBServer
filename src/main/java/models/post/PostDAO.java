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
    public void create(Integer id,  int parent, String author, String message, boolean isEdited, String forum, int thread, Timestamp created);

    /**
     * This is the method to be used to list down
     * a record from the Student table corresponding
     * to a passed student id.
     */
    /**
     * This is the method to be used to list down
     * all the records from the Student table.
     */
    public List<Post> listThread();

    /**
     * This is the method to be used to delete
     * a record from the Student table corresponding
     * to a passed student id.
     */
    public void delete();

    /**
     * This is the method to be used to update
     * a record into the Student table.
     */
    public void update(Integer id);
}
