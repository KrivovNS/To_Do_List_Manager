package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.mapper.TaskMapper;
import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = TaskService.class
)
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private TaskMapper taskMapper;

    @Test
    void updateTask_existingTaskUpdatesCompletedStatusAndSaves() {
        Task existingTask = new Task("Write tests", "Cover service layer");
        existingTask.setId(1);
        existingTask.setCompleted(false);
        existingTask.setPriority(Priority.MEDIUM);

        TaskUpdateDto updateDto = new TaskUpdateDto(
                null,
                null,
                true,
                LocalDate.now().plusDays(1),
                null,
                Set.of("testing")
        );

        when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));
        doAnswer(invocation -> {
            TaskUpdateDto dto = invocation.getArgument(0);
            Task task = invocation.getArgument(1);
            task.setCompleted(dto.completed());
            return null;
        }).when(taskMapper).updateEntity(same(updateDto), same(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1, updateDto);

        assertSame(existingTask, result);
        assertTrue(result.isCompleted());

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(1);
        verify(taskMapper).updateEntity(updateDto, existingTask);
        verify(taskRepository).save(taskCaptor.capture());
        assertSame(existingTask, taskCaptor.getValue());
    }
}
