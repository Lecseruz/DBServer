package models.status;

/**
 * Created by magomed on 20.03.17.
 */
public interface StatusDAO {

    public Status getStatus();

    public void createTable();

    public void  dropTable();
}
