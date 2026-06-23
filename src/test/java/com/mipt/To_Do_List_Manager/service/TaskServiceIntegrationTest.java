package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.exception.TaskIdsNotFoundException;
import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void bulkCompleteTasks_rollsBackWhenMissingIdPresent() {
        Task first = new Task("First", "Desc");
        first.setPriority(Priority.LOW);
        Task second = new Task("Second", "Desc");
        second.setPriority(Priority.MEDIUM);

        Task savedFirst = taskRepository.save(first);
        Task savedSecond = taskRepository.save(second);

        TaskIdsNotFoundException exception = assertThrows(
                TaskIdsNotFoundException.class,
                () -> taskService.bulkCompleteTasks(List.of(savedFirst.getId(), 999999, savedSecond.getId()))
        );
        assertEquals(999999, exception.getMissingId());
        assertEquals("Task not found for id: 999999", exception.getMessage());

        Task afterFirst = taskRepository.findById(savedFirst.getId()).orElseThrow();
        Task afterSecond = taskRepository.findById(savedSecond.getId()).orElseThrow();
        assertFalse(afterFirst.isCompleted());
        assertFalse(afterSecond.isCompleted());
    }
}


