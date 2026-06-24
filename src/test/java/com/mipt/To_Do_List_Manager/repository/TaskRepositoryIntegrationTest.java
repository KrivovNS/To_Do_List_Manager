package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("todo_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TaskRepository taskRepository;

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Test
    void findTasksDueBetween_returnsTasksFromPostgresWithinDateRange() {
        Task matchingTask = new Task("Container task", "Stored in PostgreSQL");
        matchingTask.setPriority(Priority.MEDIUM);
        matchingTask.setDueDate(LocalDate.of(2026, 7, 1));

        Task outsideRangeTask = new Task("Later task", "Outside query range");
        outsideRangeTask.setPriority(Priority.HEIGHT);
        outsideRangeTask.setDueDate(LocalDate.of(2026, 8, 1));

        Task withoutDueDateTask = new Task("No due date", "Ignored by query");
        withoutDueDateTask.setPriority(Priority.LOW);

        taskRepository.saveAll(List.of(matchingTask, outsideRangeTask, withoutDueDateTask));
        taskRepository.flush();

        List<Task> result = taskRepository.findTasksDueBetween(
                LocalDate.of(2026, 6, 24),
                LocalDate.of(2026, 7, 7)
        );

        assertThat(result)
                .extracting(Task::getTitle)
                .containsExactly("Container task");
    }
}
