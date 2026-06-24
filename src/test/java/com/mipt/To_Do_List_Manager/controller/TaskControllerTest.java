package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.dto.TaskCreateDto;
import com.mipt.To_Do_List_Manager.dto.TaskResponseDto;
import com.mipt.To_Do_List_Manager.exception.GlobalExceptionHandler;
import com.mipt.To_Do_List_Manager.mapper.TaskMapper;
import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @Test
    void createTask_validRequestReturnsCreatedTask() throws Exception {
        Task taskToCreate = task(0, "Prepare homework", "Add controller test");
        Task savedTask = task(1, "Prepare homework", "Add controller test");
        TaskResponseDto responseDto = responseDto(savedTask);

        when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(taskToCreate);
        when(taskService.addTask(taskToCreate)).thenReturn(savedTask);
        when(taskMapper.toResponseDto(savedTask)).thenReturn(responseDto);

        String requestJson = """
                {
                  "title": "Prepare homework",
                  "description": "Add controller test",
                  "dueDate": "2026-07-01",
                  "priority": "MEDIUM",
                  "tags": ["test"]
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Prepare homework"))
                .andExpect(jsonPath("$.description").value("Add controller test"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.dueDate").value("2026-07-01"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.tags[0]").value("test"));
    }

    @Test
    void getTaskById_existingTaskReturnsTask() throws Exception {
        Task savedTask = task(7, "Read docs", "Check MockMvc");
        TaskResponseDto responseDto = responseDto(savedTask);

        when(taskService.getTaskByIdOrThrow(7)).thenReturn(savedTask);
        when(taskMapper.toResponseDto(savedTask)).thenReturn(responseDto);

        mockMvc.perform(get("/api/tasks/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Read docs"))
                .andExpect(jsonPath("$.description").value("Check MockMvc"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.dueDate").value("2026-07-01"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.tags[0]").value("test"));
    }

    @Test
    void createTask_invalidRequestReturnsValidationError() throws Exception {
        String invalidJson = """
                {
                  "title": "ab",
                  "description": "Invalid title",
                  "dueDate": "2026-07-01",
                  "priority": null
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.fieldErrors.title").exists())
                .andExpect(jsonPath("$.details.fieldErrors.priority").exists());
    }

    private Task task(int id, String title, String description) {
        Task task = new Task(title, description);
        task.setId(id);
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.of(2026, 6, 24, 12, 0));
        task.setDueDate(LocalDate.of(2026, 7, 1));
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("test"));
        return task;
    }

    private TaskResponseDto responseDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getDueDate(),
                task.getPriority(),
                task.getTags()
        );
    }
}
