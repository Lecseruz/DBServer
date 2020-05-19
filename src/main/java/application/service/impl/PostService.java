package application.service.impl;

import application.models.ResponseInfoPost;
import application.dao.ForumDao;
import application.models.Post;
import application.dao.PostDao;
import application.models.PostUpdate;
import application.models.Thread;
import application.dao.ThreadDao;
import application.dao.UserDao;
import application.service.api.IPostService;
import application.service.api.IThreadService;
import application.service.api.ResourceNotFoundException;
import jdk.internal.jline.internal.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PostService implements IPostService {
    private PostDao postDao;
    private ThreadDao threadDao;
    private ForumDao forumDao;
    private UserDao userDao;
    private IThreadService threadService;

    @Autowired
    public PostService(PostDao postDao, ThreadDao threadDao, ForumDao forumDao, UserDao userDao, IThreadService threadService) {
        this.postDao = postDao;
        this.threadDao = threadDao;
        this.forumDao = forumDao;
        this.userDao = userDao;
        this.threadService = threadService;
    }

    @Override
    public Post update(int id, PostUpdate postUpdate) throws ResourceNotFoundException {
        try {
            Preconditions.checkNotNull(postUpdate);
            Post post = postDao.getPostById(id);
            Preconditions.checkNotNull(post);
            if (!post.getMessage().equals(postUpdate.getMessage()))
                return postDao.updatePost(postUpdate.getMessage(), post.getId());
            return post;
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Post not found", e);
        }
    }

    @Override
    public ResponseInfoPost getInfoPost(int id, Set<String> related) throws ResourceNotFoundException {
        try {
            final ResponseInfoPost responseInfoPost = new ResponseInfoPost();
            Post post = postDao.getPostById(id);
            Preconditions.checkNotNull(post);
            responseInfoPost.setPost(post);
            if (related != null) {
                if (related.contains("user")) {
                    responseInfoPost.setAuthor(userDao.getUserByNickname(responseInfoPost.getPost().getAuthor()));
                }
                if (related.contains("forum")) {
                    responseInfoPost.setForum(forumDao.getForumBySlug(responseInfoPost.getPost().getForum()));
                }
                if (related.contains("thread")) {
                    responseInfoPost.setThread(threadDao.getThreadById(responseInfoPost.getPost().getThread()));
                }
            }
            return responseInfoPost;
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Post not found", e);
        }
    }

    @Override
    public void createPosts(String slugOrId, List<Post> posts) {
        try {
            final Thread thread = threadService.getThread(slugOrId);
            Preconditions.checkNotNull(thread);
            for (Post post : posts) {
                Preconditions.checkNotNull(userDao.getUserByNickname(post.getAuthor()));
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
                if (post.getParent() != 0) {
                    final Post parent = postDao.getPostById(post.getParent());
                    if(parent == null)
                        throw new ResourceNotFoundException("Post not found");
                    if(thread.getId() != parent.getThread()) {
                       throw new IllegalArgumentException();
                    }
                }
            }
            postDao.createPosts(posts);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

}
