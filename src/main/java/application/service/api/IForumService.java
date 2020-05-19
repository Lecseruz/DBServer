package application.service.api;

import application.models.Forum;

public interface IForumService {
    void createForum(Forum forum) throws ResourceNotFoundException;

    Forum getForum(String slug) throws ResourceNotFoundException;
}
