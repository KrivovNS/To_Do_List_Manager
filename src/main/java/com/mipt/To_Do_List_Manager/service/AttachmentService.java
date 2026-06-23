package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.repository.TaskAttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AttachmentService {

    private final TaskAttachmentRepository taskAttachmentRepository;
    private final TaskService taskService;
    private final Path uploadPath;

    public AttachmentService(
            TaskAttachmentRepository taskAttachmentRepository,
            TaskService taskService,
            @Value("${app.attachments.upload-dir:uploads}") String uploadDir
    ) {
        this.taskAttachmentRepository = taskAttachmentRepository;
        this.taskService = taskService;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to initialize uploads directory",
                    ex
            );
        }
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
        Task task = taskService.getTaskByIdOrThrow(toIntTaskId(taskId));

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        String originalName = StringUtils.cleanPath(
                Objects.requireNonNullElse(file.getOriginalFilename(), "file")
        );
        if (originalName.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }

        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(storedFileName).normalize();
        if (!targetPath.startsWith(uploadPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid storage path");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file", ex);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName(originalName);
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(
                file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE
        );
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());

        return taskAttachmentRepository.save(attachment);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
    }

    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path path = uploadPath.resolve(attachment.getStoredFileName()).normalize();
        if (!path.startsWith(uploadPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }

        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment file not found");
            }

            return resource;
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load file", ex);
        }
    }

    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path path = uploadPath.resolve(attachment.getStoredFileName()).normalize();

        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file", ex);
        }

        taskAttachmentRepository.deleteById(attachmentId);
    }

    public List<TaskAttachment> getAttachmentsByTaskId(Long taskId) {
        int normalizedTaskId = toIntTaskId(taskId);
        taskService.getTaskByIdOrThrow(normalizedTaskId);
        return taskAttachmentRepository.findByTask_Id(normalizedTaskId);
    }

    private int toIntTaskId(Long taskId) {
        if (taskId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "taskId must not be null");
        }

        try {
            return Math.toIntExact(taskId);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "taskId is out of range", ex);
        }
    }
}
