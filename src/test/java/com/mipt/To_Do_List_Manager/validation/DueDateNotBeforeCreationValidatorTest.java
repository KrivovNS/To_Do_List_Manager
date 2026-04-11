package com.mipt.To_Do_List_Manager.validation;

import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DueDateNotBeforeCreationValidatorTest {

    @Mock
    private TaskService taskService;

    @Mock
    private HttpServletRequest request;

    private DueDateNotBeforeCreationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DueDateNotBeforeCreationValidator(taskService, request);
    }

    @Test
    void isValid_returnsFalseWhenDueDateBeforeCreationDate() {
        Task task = new Task("title", "description");
        task.setCreatedAt(LocalDateTime.of(2026, 4, 11, 10, 0));

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "1"));
        when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

        TaskUpdateDto dto = new TaskUpdateDto(
                "Updated",
                "Desc",
                false,
                LocalDate.of(2026, 4, 10),
                Priority.LOW,
                Set.of("x")
        );

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void isValid_returnsTrueWhenDueDateAfterCreationDate() {
        Task task = new Task("title", "description");
        task.setCreatedAt(LocalDateTime.of(2026, 4, 11, 10, 0));

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "1"));
        when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

        TaskUpdateDto dto = new TaskUpdateDto(
                "Updated",
                "Desc",
                false,
                LocalDate.of(2026, 4, 11),
                Priority.LOW,
                Set.of("x")
        );

        assertTrue(validator.isValid(dto, null));
    }
}
