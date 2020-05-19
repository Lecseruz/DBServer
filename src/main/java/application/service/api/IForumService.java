package application.service.api;

import application.models.Forum;
import application.service.exception.ResourceNotFoundException;

public interface IForumService {
    void createForum(Forum forum) throws ResourceNotFoundException;

    Forum getForum(String slug) throws ResourceNotFoundException;
}
