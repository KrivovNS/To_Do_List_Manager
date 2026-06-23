package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import com.mipt.To_Do_List_Manager.repository.TaskAttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttachmentServiceTest {

    private Path tempDir;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskAttachmentRepository repository;

    private AttachmentService attachmentService;
    private final AtomicLong sequence = new AtomicLong(0);
    private final List<TaskAttachment> storage = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Paths.get("target", "test-uploads", UUID.randomUUID().toString()).toAbsolutePath().normalize();
        Files.createDirectories(tempDir);
        attachmentService = new AttachmentService(repository, taskService, tempDir.toString());

        doAnswer(invocation -> {
            TaskAttachment attachment = invocation.getArgument(0);
            if (attachment.getId() == null) {
                attachment.setId(sequence.incrementAndGet());
            }
            storage.removeIf(existing -> existing.getId().equals(attachment.getId()));
            storage.add(attachment);
            return attachment;
        }).when(repository).save(any(TaskAttachment.class));

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return storage.stream().filter(attachment -> attachment.getId().equals(id)).findFirst();
        }).when(repository).findById(any(Long.class));

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            storage.removeIf(attachment -> attachment.getId().equals(id));
            return null;
        }).when(repository).deleteById(any(Long.class));

        doAnswer(invocation -> {
            Integer taskId = invocation.getArgument(0);
            return storage.stream()
                    .filter(attachment -> attachment.getTask() != null)
                    .filter(attachment -> attachment.getTask().getId() != null)
                    .filter(attachment -> attachment.getTask().getId().equals(taskId))
                    .toList();
        }).when(repository).findByTask_Id(any(Integer.class));
    }

    @Test
    void storeAttachment_success() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        when(taskService.getTaskByIdOrThrow(1)).thenReturn(task);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                "hello".getBytes()
        );

        TaskAttachment attachment = attachmentService.storeAttachment(1L, file);

        assertTrue(attachment.getId() > 0);
        assertEquals("sample.txt", attachment.getFileName());
        assertEquals("text/plain", attachment.getContentType());
        assertTrue(Files.exists(tempDir.resolve(attachment.getStoredFileName())));
    }

    @Test
    void loadAsResource_success() throws Exception {
        Task task = new Task("Task", "Description");
        task.setId(1);
        when(taskService.getTaskByIdOrThrow(1)).thenReturn(task);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "payload.txt",
                "text/plain",
                "payload".getBytes()
        );
        TaskAttachment attachment = attachmentService.storeAttachment(1L, file);

        Resource resource = attachmentService.loadAsResource(attachment.getId());

        assertTrue(resource.exists());
        assertArrayEquals("payload".getBytes(), resource.getContentAsByteArray());
    }

    @Test
    void deleteAttachment_success() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        when(taskService.getTaskByIdOrThrow(1)).thenReturn(task);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "delete.txt",
                "text/plain",
                "delete-me".getBytes()
        );
        TaskAttachment attachment = attachmentService.storeAttachment(1L, file);
        Path storedPath = tempDir.resolve(attachment.getStoredFileName());

        attachmentService.deleteAttachment(attachment.getId());

        assertFalse(Files.exists(storedPath));
        assertTrue(storage.stream().noneMatch(existing -> existing.getId().equals(attachment.getId())));
    }

    @Test
    void storeAttachment_emptyFile_badRequest() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        when(taskService.getTaskByIdOrThrow(1)).thenReturn(task);

        MockMultipartFile empty = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.storeAttachment(1L, empty)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void storeAttachment_taskNotFound() {
        when(taskService.getTaskByIdOrThrow(999)).thenThrow(new com.mipt.To_Do_List_Manager.exception.TaskNotFoundException("Task not found"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                "hello".getBytes()
        );

        assertThrows(
                com.mipt.To_Do_List_Manager.exception.TaskNotFoundException.class,
                () -> attachmentService.storeAttachment(999L, file)
        );
    }

    @Test
    void getAttachmentsByTaskId_returnsAttachments() {
        Task task = new Task("Task", "Description");
        task.setId(7);
        when(taskService.getTaskByIdOrThrow(7)).thenReturn(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("a.txt");
        attachment.setStoredFileName("stored-a.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(2);
        attachment.setUploadedAt(java.time.LocalDateTime.now());
        repository.save(attachment);

        List<TaskAttachment> result = attachmentService.getAttachmentsByTaskId(7L);
        assertEquals(1, result.size());
        assertEquals("a.txt", result.getFirst().getFileName());
    }
}
