package com.mipt.To_Do_List_Manager.mapper;

import com.mipt.To_Do_List_Manager.dto.TaskCreateDto;
import com.mipt.To_Do_List_Manager.dto.TaskResponseDto;
import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void toEntity_mapsAllFields() {
        TaskCreateDto dto = new TaskCreateDto(
                "Mapper title",
                "Mapper description",
                LocalDate.now().plusDays(1),
                Priority.LOW,
                Set.of("a", "b")
        );

        Task task = mapper.toEntity(dto);

        assertEquals(dto.title(), task.getTitle());
        assertEquals(dto.description(), task.getDescription());
        assertEquals(dto.dueDate(), task.getDueDate());
        assertEquals(dto.priority(), task.getPriority());
        assertEquals(dto.tags(), task.getTags());
    }

    @Test
    void updateEntity_ignoresNulls() {
        Task task = new Task("Old", "Old description");
        task.setCompleted(false);
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("old"));

        TaskUpdateDto dto = new TaskUpdateDto(
                "New title",
                null,
                null,
                null,
                null,
                Set.of("new")
        );

        mapper.updateEntity(dto, task);

        assertEquals("New title", task.getTitle());
        assertEquals("Old description", task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertEquals(Set.of("new"), task.getTags());
    }

    @Test
    void toResponseDto_mapsAllFields() {
        Task task = new Task("Response title", "Response description");
        task.setId(10);
        task.setCompleted(true);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(LocalDate.now().plusDays(3));
        task.setPriority(Priority.HEIGHT);
        task.setTags(Set.of("x"));

        TaskResponseDto dto = mapper.toResponseDto(task);

        assertEquals(task.getId(), dto.id());
        assertEquals(task.getTitle(), dto.title());
        assertEquals(task.getDescription(), dto.description());
        assertTrue(dto.completed());
        assertEquals(task.getCreatedAt(), dto.createdAt());
        assertEquals(task.getDueDate(), dto.dueDate());
        assertEquals(task.getPriority(), dto.priority());
        assertEquals(task.getTags(), dto.tags());
    }
}
