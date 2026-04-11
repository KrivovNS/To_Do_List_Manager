package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.dto.AttachmentResponseDto;
import com.mipt.To_Do_List_Manager.dto.ErrorResponse;
import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import com.mipt.To_Do_List_Manager.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Attachments", description = "Task attachment upload/download operations")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload attachment for task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Attachment uploaded",
                    content = @Content(schema = @Schema(implementation = AttachmentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AttachmentResponseDto> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file
    ) {
        TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponseDto(attachment));
    }

    @GetMapping("/attachments/{attachmentId}")
    @Operation(summary = "Download attachment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment stream returned"),
            @ApiResponse(responseCode = "404", description = "Attachment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(attachment.getFileName(), StandardCharsets.UTF_8)
                .build();

        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
        String storedContentType = attachment.getContentType();
        if (storedContentType != null) {
            try {
                contentType = MediaType.parseMediaType(storedContentType);
            } catch (IllegalArgumentException ignored) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(attachment.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    @Operation(summary = "Delete attachment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Attachment deleted"),
            @ApiResponse(responseCode = "404", description = "Attachment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{taskId}/attachments")
    @Operation(summary = "Get list of task attachments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment metadata returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttachmentResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AttachmentResponseDto>> getTaskAttachments(@PathVariable Long taskId) {
        List<AttachmentResponseDto> response = attachmentService.getAttachmentsByTaskId(taskId).stream()
                .map(this::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    private AttachmentResponseDto toResponseDto(TaskAttachment attachment) {
        return new AttachmentResponseDto(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getSize(),
                attachment.getUploadedAt()
        );
    }
}
