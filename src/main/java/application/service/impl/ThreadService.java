package application.service.impl;

import application.models.ResponsePosts;
import application.models.Forum;
import application.models.Post;
import application.dao.PostDao;
import application.models.Thread;
import application.dao.ThreadDao;
import application.models.ThreadUpdate;
import application.service.api.DuplicateResourceException;
import application.service.api.IThreadService;
import application.service.api.ResourceNotFoundException;
import jdk.internal.jline.internal.Preconditions;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThreadService implements IThreadService {
    public static final String THREAD_NOT_FOUND = "Thread not found";
    public static final String THREAD_ALREADY_EXIST = "Thread already exist";
    private ThreadDao threadDao;
    private UserService userService;
    private PostDao postDao;

    public ThreadService(ThreadDao threadDao, UserService userService, ForumService forumService, PostDao postDao) {
        this.threadDao = threadDao;
        this.userService = userService;
        this.forumService = forumService;
        this.postDao = postDao;
    }

    private ForumService forumService;

    @Override
    public void create(String slug, Thread thread) throws ResourceNotFoundException {
        try {
            Preconditions.checkNotNull(thread);
            Preconditions.checkNotNull(userService.getUser(thread.getAuthor()));
            Preconditions.checkNotNull(forumService.getForum(thread.getSlug()));
            thread.setSlug(slug);
            threadDao.create(thread);
        } catch (DuplicateKeyException e) {
            throw new DuplicateResourceException(THREAD_ALREADY_EXIST, e);
        }
    }

    @Override
    public Thread getThread(String slugOrId) throws ResourceNotFoundException {
        try {
            return getThreadBySlugOrId(slugOrId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(THREAD_NOT_FOUND, e);
        }
    }

    @Override
    public List<Thread> getThreads(String slug, boolean desc, int limit, String created) {
        try {
            final Forum forum = forumService.getForum(slug);
            return threadDao.getThreads(forum.getId(), desc, limit, created);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(THREAD_NOT_FOUND, e);
        }
    }

    @Override
    public Thread updateThreads(String slugOrId, ThreadUpdate threadUpdate) {
        try {
            final Thread thread = getThreadBySlugOrId(slugOrId);
            return threadDao.updateThread(threadUpdate, thread.getSlug());
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(THREAD_NOT_FOUND, e);
        } catch (DuplicateKeyException e) {
            throw new DuplicateResourceException(THREAD_ALREADY_EXIST, e);
        }
    }

    public ResponsePosts getPostsOfThread(String slugOrId, Integer limit, String marker, String sort, boolean desc) {
        final Thread thread = getThread(slugOrId);
        List<Post> posts = null;
        int markerInt = Integer.parseInt(marker);
        switch (sort) {
            case "flat": {
                posts = postDao.flatSort(thread.getId(), limit, markerInt, desc);
                if (!posts.isEmpty()) {
                    markerInt += posts.size();
                }
                break;
            }
            case "tree": {
                posts = postDao.treeSort(thread.getId(), limit, markerInt, desc);
                if (!posts.isEmpty()) {
                    markerInt += posts.size();
                }
                break;
            }
            case "parent_tree": {
                final List<Integer> parents = postDao.getParents(thread.getId(), markerInt, limit, desc);
                if (!parents.isEmpty()) {
                    markerInt += parents.size();
                }
                posts = postDao.parentTreeSort(thread.getId(), desc, parents);
                break;
            }
            default:
                break;
        }
        return new ResponsePosts(String.valueOf(markerInt), posts);
    }

    public Thread getThreadBySlugOrId(String slug) {
        if (slug.matches("[-+]?\\d*\\.?\\d+")) {
            return threadDao.getThreadById(Integer.parseInt(slug));
        } else {
            return threadDao.getThreadBySlug(slug);
        }
    }
}
