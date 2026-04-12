package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.dto.TaskCreateDto;
import com.mipt.To_Do_List_Manager.dto.ErrorResponse;
import com.mipt.To_Do_List_Manager.dto.TaskResponseDto;
import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.mapper.TaskMapper;
import com.mipt.To_Do_List_Manager.marker.OnCreate;
import com.mipt.To_Do_List_Manager.marker.OnUpdate;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.service.TaskService;
import com.mipt.To_Do_List_Manager.validation.DueDateNotBeforeCreation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Tag(name = "Tasks", description = "Task CRUD operations")
public class TaskController {

    private final TaskService service;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.service = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class))))
    })
    public ResponseEntity<List<TaskResponseDto>> getAll() {
        List<Task> tasks = service.getAllTasks();
        List<TaskResponseDto> dtoList = tasks.stream()
                .map(taskMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(dtoList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task returned",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable int id) {
        Task task = service.getTaskByIdOrThrow(id);
        return ResponseEntity.ok(taskMapper.toResponseDto(task));
    }

    @PostMapping()
    @Operation(summary = "Create task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponseDto> addTask(@RequestBody @Validated(OnCreate.class) TaskCreateDto taskDto) {
        Task createdTask = service.addTask(taskMapper.toEntity(taskDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toResponseDto(createdTask));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable int id,
            @RequestBody @Validated(OnUpdate.class) @DueDateNotBeforeCreation TaskUpdateDto taskDto
    ) {
        Task updatedTask = service.updateTask(id, taskDto);
        return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
