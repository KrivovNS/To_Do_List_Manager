package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.model.Task;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoritesService {

    public static final String FAVORITE_TASK_IDS_SESSION_ATTRIBUTE = "favoriteTaskIds";

    private final TaskService taskService;

    public FavoritesService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void addToFavorites(Long taskId, HttpSession session) {
        int normalizedTaskId = toIntTaskId(taskId);

        if (taskService.getTaskById(normalizedTaskId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }

        Set<Integer> favoriteTaskIds = getMutableFavoriteTaskIds(session);
        favoriteTaskIds.add(normalizedTaskId);
        session.setAttribute(FAVORITE_TASK_IDS_SESSION_ATTRIBUTE, favoriteTaskIds);
    }

    public void removeFromFavorites(Long taskId, HttpSession session) {
        int normalizedTaskId = toIntTaskId(taskId);

        Set<Integer> favoriteTaskIds = getMutableFavoriteTaskIds(session);
        favoriteTaskIds.remove(normalizedTaskId);
        session.setAttribute(FAVORITE_TASK_IDS_SESSION_ATTRIBUTE, favoriteTaskIds);
    }

    public Set<Integer> getFavoriteTaskIds(HttpSession session) {
        return new LinkedHashSet<>(getMutableFavoriteTaskIds(session));
    }

    public List<Task> getFavoriteTasks(HttpSession session) {
        Set<Integer> favoriteTaskIds = getMutableFavoriteTaskIds(session);
        List<Task> favoriteTasks = new ArrayList<>();

        for (Integer taskId : favoriteTaskIds) {
            taskService.getTaskById(taskId).ifPresent(favoriteTasks::add);
        }

        return favoriteTasks;
    }

    private Set<Integer> getMutableFavoriteTaskIds(HttpSession session) {
        Object sessionValue = session.getAttribute(FAVORITE_TASK_IDS_SESSION_ATTRIBUTE);
        if (sessionValue == null) {
            Set<Integer> empty = new LinkedHashSet<>();
            session.setAttribute(FAVORITE_TASK_IDS_SESSION_ATTRIBUTE, empty);
            return empty;
        }

        if (sessionValue instanceof Set<?>) {
            Set<Integer> result = new LinkedHashSet<>();
            for (Object value : ((Set<?>) sessionValue)) {
                if (value instanceof Integer intValue) {
                    result.add(intValue);
                }
            }
            return result;
        }

        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Session attribute favoriteTaskIds has invalid type"
        );
    }

    private int toIntTaskId(Long taskId) {
        if (taskId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "taskId must not be null");
        }

        try {
            return Math.toIntExact(taskId);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "taskId is out of range", ex);
        }
    }
}
