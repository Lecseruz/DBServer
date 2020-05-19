package application.service.api;

import application.models.ResponsePosts;
import application.models.Thread;
import application.models.ThreadUpdate;
import application.service.exception.DuplicateResourceException;
import application.service.exception.ResourceNotFoundException;

import java.util.List;

public interface IThreadService {
    void create(String slug, Thread thread) throws DuplicateResourceException;

    Thread getThread(String slug) throws ResourceNotFoundException;

    List<Thread> getThreads(String slug, boolean desc, int limit, String created) throws ResourceNotFoundException;

    Thread updateThreads(String slugORId, ThreadUpdate threadUpdate);

    ResponsePosts getPostsOfThread(String slugOrId, Integer limit, String marker, String sort, boolean desc);
}
