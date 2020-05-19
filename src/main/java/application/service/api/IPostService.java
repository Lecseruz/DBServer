package application.service.api;

import application.models.ResponseInfoPost;
import application.models.Post;
import application.models.PostUpdate;

import java.util.List;
import java.util.Set;

public interface IPostService {
    Post update(int id, PostUpdate postUpdate) throws ResourceNotFoundException;

    ResponseInfoPost getInfoPost(int id, Set<String> related) throws ResourceNotFoundException;

    void createPosts(String slugOrId, List<Post> posts);
}
