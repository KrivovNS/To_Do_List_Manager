package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryDataJpaTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Test
    void findByCompletedAndPriority_returnsFilteredTasks() {
        Task matching = new Task("A", "Desc");
        matching.setPriority(Priority.LOW);
        matching.setCompleted(true);

        Task notMatching = new Task("B", "Desc");
        notMatching.setPriority(Priority.MEDIUM);
        notMatching.setCompleted(true);

        taskRepository.save(matching);
        taskRepository.save(notMatching);

        List<Task> result = taskRepository.findByCompletedAndPriority(true, Priority.LOW);

        assertEquals(1, result.size());
        assertEquals("A", result.getFirst().getTitle());
    }

    @Test
    void findTasksDueBetween_returnsTasksForNext7Days() {
        Task dueSoon = new Task("Soon", "Desc");
        dueSoon.setPriority(Priority.MEDIUM);
        dueSoon.setDueDate(LocalDate.now().plusDays(3));

        Task dueLate = new Task("Late", "Desc");
        dueLate.setPriority(Priority.HEIGHT);
        dueLate.setDueDate(LocalDate.now().plusDays(20));

        taskRepository.save(dueSoon);
        taskRepository.save(dueLate);

        List<Task> result = taskRepository.findTasksDueBetween(LocalDate.now(), LocalDate.now().plusDays(7));

        assertEquals(1, result.size());
        assertEquals("Soon", result.getFirst().getTitle());
    }

    @Test
    void saveTaskWithAttachment_andFindByTaskIdWorks() {
        Task task = new Task("Task", "Desc");
        task.setPriority(Priority.LOW);
        task.setTags(Set.of("one"));
        Task savedTask = taskRepository.save(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(savedTask);
        attachment.setFileName("file.txt");
        attachment.setStoredFileName("stored-file.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(10L);
        attachment.setUploadedAt(java.time.LocalDateTime.now());
        taskAttachmentRepository.save(attachment);

        List<TaskAttachment> attachments = taskAttachmentRepository.findByTask_Id(savedTask.getId());
        assertEquals(1, attachments.size());

        assertTrue(taskRepository.findById(savedTask.getId()).isPresent());
        assertFalse(attachments.isEmpty());
    }
}


