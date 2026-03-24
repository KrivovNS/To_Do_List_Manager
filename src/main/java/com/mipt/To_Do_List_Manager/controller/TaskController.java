package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.service.TaskService;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.dto.TaskDto;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления задачами.
 * Предоставляет endpoint`s для выполнения основных CRUD-операций.
 * Базовый путь: /api/tasks.
 * Endpoint`s:
 *      GET /api/tasks - получить все задачи
 *      GET /api/tasks/{id} - получить задачу по ID
 *      POST /api/tasks - создать новую задачу
 *      PUT /api/tasks/{id} - обновить задачу
 *      DELETE /api/tasks/{id} - удалить задачу
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    TaskService service;

    @Autowired
    public TaskController(TaskService taskService) {
        this.service = taskService;
    }

    @GetMapping
    public List<Task> getAll() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public Optional<Task> getTaskById(@PathVariable int id) {
        return service.getTaskById(id);
    }

    @PostMapping()
    public void addTask(@RequestBody TaskDto taskDto) {
        service.addTask(new Task(taskDto.getTitle(), taskDto.getDescription()));
    }

    @PutMapping("/{id}")
    public void updateTask(@PathVariable int id, @RequestBody TaskDto taskDto) {
        service.updateTask(id, new Task(taskDto.getTitle(), taskDto.getDescription()));
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable int id) {
        service.deleteTask(id);
    }
}
