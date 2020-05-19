package application.service.impl;

import application.models.Forum;
import application.models.User;
import application.dao.UserDao;
import application.service.exception.DuplicateResourceException;
import application.service.api.IUserService;
import application.service.exception.ResourceNotFoundException;
import jdk.internal.jline.internal.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    private UserDao userDao;
    private ForumService forumService;

    @Autowired
    public UserService(UserDao userDao, ForumService forumService) {
        this.userDao = userDao;
        this.forumService = forumService;
    }

    @Override
    public User getUser(String nickname) {
        try {
            return userDao.getUserByNickname(nickname);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("User not found", e);
        }
    }

    @Override
    public List<User> getByForum(String slug, int limit, String since, boolean desc) {
        try {
            final Forum forum = forumService.getForum(slug);
            return userDao.getByForum(forum.getId(), limit, since, desc);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Users not found", e);
        }
    }

    @Override
    public User create(String nickname, User user) {
        try {
            Preconditions.checkNotNull(user);
            user.setNickname(nickname);
            userDao.create(user);
            return user;
        } catch (DuplicateKeyException e) {
            throw new DuplicateResourceException("User already exist", e);
        }
    }

    @Override
    public List<User> getUsersByNicknameOrEmail(String nickname, String email) {
        try {
            return userDao.getUsersByNicknameOrEmail(nickname, email);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Users not found", e);
        }
    }

    @Override
    public User update(String nickname, User user) {
        try {
            Preconditions.checkNotNull(user);
            user.setNickname(nickname);
            return userDao.update(user);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Users not found", e);
        } catch (DuplicateResourceException e) {
            throw new DuplicateResourceException("User already exist", e);
        }
    }
}
