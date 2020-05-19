package application.service.api;

import application.models.User;
import application.service.exception.ResourceNotFoundException;

import java.util.List;

public interface IUserService {
    User getUser(String nickname) throws ResourceNotFoundException;

    List<User> getByForum(String slug, int limit, String since, boolean desc) throws ResourceNotFoundException;

    User create(String nickname, User user) throws ResourceNotFoundException;

    List<User> getUsersByNicknameOrEmail(String nickname, String email) throws ResourceNotFoundException;

    User update(String nickname, User user) throws ResourceNotFoundException;
}
