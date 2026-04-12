package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.exception.GlobalExceptionHandler;
import com.mipt.To_Do_List_Manager.mapper.TaskMapper;
import com.mipt.To_Do_List_Manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerValidationWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    void createTask_invalidBody_returns400WithFieldErrors() throws Exception {
        String invalidJson = """
                {
                  "title": "a",
                  "description": "desc",
                  "dueDate": "2026-04-11",
                  "priority": null,
                  "tags": ["x"]
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

    @Test
    void updateTask_invalidTitleForOnUpdate_returns400() throws Exception {
        String invalidJson = """
                {
                  "title": "ab"
                }
                """;

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
