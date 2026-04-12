package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.exception.TaskNotFoundException;
import com.mipt.To_Do_List_Manager.mapper.TaskMapper;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private Map<String, Task> taskCache;

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    private final Logger log = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @PostConstruct
    public void initCache() {
        log.info("Initializing cache for application: {} v{}", appName, appVersion);
        taskCache = new HashMap<>();

        addTask(new Task("Wise thought 1", "Don`t miss deadlines"));
        addTask(new Task("Wise thought 2", "Do everything on time"));
        addTask(new Task("Wise thought 3", "Enjoy the moment"));

        for (Task task : taskRepository.getAll()) {
            taskCache.put(Integer.toString(task.getId()), task);
        }

        log.info("Cache initialized with {} tasks", taskCache.size());
    }

    @PreDestroy
    public void clearCache() {
        int size = 0;

        if (taskCache != null) {
            size = taskCache.size();
            taskCache.clear();
        }

        log.info("DESTROY CacheService. Cache size before destroy: {}", size);
    }

    public Optional<Task> getTaskById(int id) {
        return taskRepository.get(id);
    }

    public Task addTask(Task task) {
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }

        taskRepository.add(task);
        return task;
    }

    public void deleteTask(int id) {
        getTaskByIdOrThrow(id);
        taskRepository.delete(id);
    }

    public boolean containsTask(Task task) {
        return taskRepository.contains(task);
    }

    public Task updateTask(int id, TaskUpdateDto taskDto) {
        Task existingTask = getTaskByIdOrThrow(id);
        taskMapper.updateEntity(taskDto, existingTask);
        taskRepository.update(id, existingTask);
        return existingTask;
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }

    public Task getTaskByIdOrThrow(int id) {
        return taskRepository.get(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }
}
