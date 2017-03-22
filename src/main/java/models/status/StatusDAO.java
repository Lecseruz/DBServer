package models.status;

/**
 * Created by magomed on 20.03.17.
 */
public interface StatusDAO {
    public void updateCountForum(Integer integer);
    public void updateCountUser(Integer integer);
    public void updateCountThread(Integer integer);
    public void updateCountPosts(Integer integer);
    public void delete();
}
