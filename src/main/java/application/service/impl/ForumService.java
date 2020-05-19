package application.service.impl;

import application.models.Forum;
import application.dao.ForumDao;
import application.service.exception.ResourceNotFoundException;
import application.service.api.IForumService;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ForumService implements IForumService {
    private ForumDao forumDao;
    private UserService userService;

    @Autowired
    public ForumService(ForumDao forumDao, UserService userService) {
        this.forumDao = forumDao;
        this.userService = userService;
    }

    @Override
    public void createForum(Forum forum) throws ResourceNotFoundException {
        try {
            Preconditions.checkNotNull(forum);
            Preconditions.checkNotNull(userService.getUser(forum.getUser()));
            forumDao.create(forum.getTitle(), forum.getUser(), forum.getSlug());
        } catch (DuplicateKeyException e) {
            throw new ResourceNotFoundException("Forum already exist", e);
        }
    }

    @Override
    public Forum getForum(String slug) {
        try {
            Preconditions.checkNotNull(slug);
            return forumDao.getForumBySlug(slug);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Forum not found", e);
        }
    }
}
