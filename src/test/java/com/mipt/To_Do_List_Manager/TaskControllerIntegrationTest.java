package com.mipt.To_Do_List_Manager;

import com.mipt.To_Do_List_Manager.dto.ErrorResponse;
import com.mipt.To_Do_List_Manager.dto.TaskCreateDto;
import com.mipt.To_Do_List_Manager.dto.TaskResponseDto;
import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/tasks";
    }

    @Test
    void createUpdateDeleteTask_happyPath() {
        TaskCreateDto createDto = new TaskCreateDto(
                "Integration task",
                "Task from integration test",
                LocalDate.now().plusDays(1),
                Priority.MEDIUM,
                Set.of("test")
        );

        ResponseEntity<TaskResponseDto> createResponse =
                restTemplate.postForEntity(baseUrl, createDto, TaskResponseDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().id());

        int taskId = createResponse.getBody().id();
        TaskUpdateDto updateDto = new TaskUpdateDto(
                "Updated integration task",
                null,
                null,
                LocalDate.now().plusDays(2),
                Priority.HEIGHT,
                Set.of("updated")
        );

        ResponseEntity<TaskResponseDto> updateResponse = restTemplate.exchange(
                baseUrl + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                TaskResponseDto.class
        );
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Updated integration task", updateResponse.getBody().title());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + taskId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    void getTaskById_notFound() {
        ResponseEntity<ErrorResponse> response =
                restTemplate.getForEntity(baseUrl + "/999999", ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertTrue(response.getBody().message().toLowerCase().contains("task"));
    }
}
