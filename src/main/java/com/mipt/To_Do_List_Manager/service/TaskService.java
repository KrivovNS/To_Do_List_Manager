package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private Map<String, Task> taskCache;

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    private final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void initCache() {
        log.info("Initializing cache for application: {} v{}", appName, appVersion);
        taskCache = new HashMap<>();

        taskRepository.add(new Task("Wise thought 1", "Don`t miss deadlines"));
        taskRepository.add(new Task("Wise thought 2", "Do everything on time"));
        taskRepository.add(new Task("Wise thought 3", "Enjoy the moment"));

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

    public void addTask(Task task) {
        taskRepository.add(task);
    }

    public void deleteTask(int id) {
        taskRepository.delete(id);
    }

    public boolean containsTask(Task task) {
        return taskRepository.contains(task);
    }

    public void updateTask(int id, Task task) {
        taskRepository.update(id, task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }
}
