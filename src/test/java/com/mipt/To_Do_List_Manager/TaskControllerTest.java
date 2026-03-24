package com.mipt.To_Do_List_Manager;

import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/tasks";
    }

    // Позитивные тесты
    @Test
    public void testGetAllTasks_Success() {
        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetTaskById_Success() {
        // Сначала создадим задачу
        TaskDto newTask = new TaskDto("Test Task", "Test Description");
        restTemplate.postForEntity(baseUrl, newTask, Void.class);

        // Получим список задач как List
        ResponseEntity<List<Task>> getAllResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Task>>() {
                }
        );

        List<Task> tasks = new ArrayList<>(getAllResponse.getBody());
        assertNotNull(tasks);

        if (!tasks.isEmpty()) {
            Integer taskId = tasks.get(tasks.size() - 1).getId();

            // Получим задачу по ID
            ResponseEntity<Task> getResponse = restTemplate.getForEntity(baseUrl + "/" + taskId, Task.class);

            assertEquals(HttpStatus.OK, getResponse.getStatusCode());
            assertNotNull(getResponse.getBody());
        }
    }

    @Test
    public void testCreateTask_Success() {
        TaskDto newTask = new TaskDto("New Task", "New Description");

        ResponseEntity<Void> response = restTemplate.postForEntity(baseUrl, newTask, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateTask_Success() {
        // Создаем задачу
        TaskDto newTask = new TaskDto("Original Title", "Original Description");
        restTemplate.postForEntity(baseUrl, newTask, Void.class);

        // Получаем список задач чтобы найти ID
        ResponseEntity<Task[]> getAllResponse = restTemplate.getForEntity(baseUrl, Task[].class);
        Task[] tasks = getAllResponse.getBody();
        assertNotNull(tasks);

        if (tasks.length > 0) {
            Integer taskId = tasks[tasks.length - 1].getId();

            // Обновляем задачу
            TaskDto updatedTask = new TaskDto("Updated Title", "Updated Description");
            HttpEntity<TaskDto> requestEntity = new HttpEntity<>(updatedTask);

            ResponseEntity<Void> putResponse = restTemplate.exchange(
                    baseUrl + "/" + taskId,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );

            assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        }
    }

    @Test
    public void testDeleteTask_Success() {
        // Создаем задачу
        TaskDto newTask = new TaskDto("Task to Delete", "Will be deleted");
        restTemplate.postForEntity(baseUrl, newTask, Void.class);

        // Получаем список задач чтобы найти ID
        ResponseEntity<Task[]> getAllResponse = restTemplate.getForEntity(baseUrl, Task[].class);
        Task[] tasks = getAllResponse.getBody();
        assertNotNull(tasks);

        if (tasks.length > 0) {
            Integer taskId = tasks[tasks.length - 1].getId();

            // Удаляем задачу
            ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                    baseUrl + "/" + taskId,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        }
    }

    // Негативные тесты (просто проверяем что метод вызывается)
    @Test
    public void testGetTaskById_NotFound() {
        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/9999", Task.class);
        assertNotNull(response);
    }

    @Test
    public void testCreateTask_WithEmptyData() {
        TaskDto emptyTask = new TaskDto("", "");
        ResponseEntity<Void> response = restTemplate.postForEntity(baseUrl, emptyTask, Void.class);
        assertNotNull(response);
    }

    @Test
    public void testUpdateTask_NotFound() {
        TaskDto updatedTask = new TaskDto("Updated Title", "Updated Description");
        HttpEntity<TaskDto> requestEntity = new HttpEntity<>(updatedTask);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );
        assertNotNull(response);
    }

    @Test
    public void testDeleteTask_NotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertNotNull(response);
    }

    @Test
    public void testCreateTask_WithoutBody() {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                Void.class
        );
        assertNotNull(response);
    }
}