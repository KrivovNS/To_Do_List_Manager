package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import com.mipt.To_Do_List_Manager.repository.InMemoryTaskAttachmentRepository;
import com.mipt.To_Do_List_Manager.repository.TaskAttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private TaskService taskService;

    private TaskAttachmentRepository repository;
    private AttachmentService attachmentService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskAttachmentRepository();
        attachmentService = new AttachmentService(repository, taskService, tempDir.toString());
    }

    @Test
    void storeAttachment_success() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

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
        when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

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
        when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

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
        assertTrue(repository.findById(attachment.getId()).isEmpty());
    }

    @Test
    void storeAttachment_emptyFile_badRequest() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

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
        when(taskService.getTaskById(999)).thenReturn(Optional.empty());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                "hello".getBytes()
        );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.storeAttachment(999L, file)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
