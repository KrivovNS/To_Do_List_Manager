package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.exception.TaskIdsNotFoundException;
import com.mipt.To_Do_List_Manager.exception.TaskNotFoundException;
import com.mipt.To_Do_List_Manager.mapper.TaskMapper;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public Optional<Task> getTaskById(int id) {
        return taskRepository.findById(id);
    }

    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(int id) {
        Task task = getTaskByIdOrThrow(id);
        taskRepository.delete(task);
    }

    public Task updateTask(int id, TaskUpdateDto taskDto) {
        Task existingTask = getTaskByIdOrThrow(id);
        taskMapper.updateEntity(taskDto, existingTask);
        return taskRepository.save(existingTask);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskByIdOrThrow(int id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    public List<Task> getTasksDueWithin7Days() {
        LocalDate now = LocalDate.now();
        return taskRepository.findTasksDueBetween(now, now.plusDays(7));
    }

    public List<Task> getAllTasksWithAttachments() {
        return taskRepository.findAllWithAttachments();
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = TaskIdsNotFoundException.class
    )
    public void bulkCompleteTasks(List<Integer> ids) {
        for (Integer taskId : ids) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskIdsNotFoundException(taskId));

            task.setCompleted(true);
        }
    }
}
