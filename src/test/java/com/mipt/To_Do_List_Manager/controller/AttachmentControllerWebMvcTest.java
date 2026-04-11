package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.exception.GlobalExceptionHandler;
import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import com.mipt.To_Do_List_Manager.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttachmentController.class)
@Import(GlobalExceptionHandler.class)
class AttachmentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @Test
    void uploadAttachment_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.txt",
                "text/plain",
                "hello".getBytes()
        );

        TaskAttachment attachment = new TaskAttachment();
        attachment.setId(1L);
        attachment.setTaskId(5L);
        attachment.setFileName("doc.txt");
        attachment.setStoredFileName("uuid.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(5L);
        attachment.setUploadedAt(LocalDateTime.of(2026, 4, 11, 12, 0));

        when(attachmentService.storeAttachment(eq(5L), any())).thenReturn(attachment);

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 5L).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("doc.txt"));
    }

    @Test
    void uploadAttachment_taskNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.txt",
                "text/plain",
                "hello".getBytes()
        );

        when(attachmentService.storeAttachment(eq(5L), any())).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
        );

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 5L).file(file))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void downloadAttachment_success() throws Exception {
        TaskAttachment attachment = new TaskAttachment();
        attachment.setId(10L);
        attachment.setFileName("report.pdf");
        attachment.setContentType("application/pdf");
        attachment.setSize(4L);

        when(attachmentService.getAttachment(10L)).thenReturn(attachment);
        when(attachmentService.loadAsResource(10L)).thenReturn(new ByteArrayResource("data".getBytes()));

        mockMvc.perform(get("/api/attachments/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("report.pdf")));
    }

    @Test
    void deleteAttachment_success() throws Exception {
        mockMvc.perform(delete("/api/attachments/{id}", 3L))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTaskAttachments_success() throws Exception {
        TaskAttachment first = new TaskAttachment();
        first.setId(1L);
        first.setFileName("a.txt");
        first.setSize(2L);
        first.setUploadedAt(LocalDateTime.of(2026, 4, 11, 12, 0));

        TaskAttachment second = new TaskAttachment();
        second.setId(2L);
        second.setFileName("b.txt");
        second.setSize(3L);
        second.setUploadedAt(LocalDateTime.of(2026, 4, 11, 12, 1));

        when(attachmentService.getAttachmentsByTaskId(7L)).thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/tasks/{taskId}/attachments", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].fileName").value("b.txt"));
    }

    @Test
    void deleteAttachment_notFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"))
                .when(attachmentService).deleteAttachment(99L);

        mockMvc.perform(delete("/api/attachments/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
