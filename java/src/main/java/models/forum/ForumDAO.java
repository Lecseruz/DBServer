package models.forum;

import java.util.List;

public interface ForumDAO {
    /**
     * This is the method to be used to create
     * a record in the Student table.
     */
    public void createTable();

    public void create(String title, String name, String slug, int posts, int threads);

    public void  dropTable();
    /**
     * This is the method to be used to list down
     * a record from the Student table corresponding
     * to a passed student id.
     */
    /**
     * This is the method to be used to list down
     * all the records from the Student table.
     */
    public List<Forum> listForum();

    public Forum getForumBySlug(String slug);

    public Forum getForum(String nickname, String title);

    public int getCount();

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
    public void update(String title, String name, String slug, int posts, int threads);
}
