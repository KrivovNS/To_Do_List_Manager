package com.mipt.To_Do_List_Manager.dto;

import com.mipt.To_Do_List_Manager.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(name = "TaskResponseDto", description = "Task data returned by API")
public record TaskResponseDto(int id,
                              @Schema(description = "Task title", example = "Finish Spring homework")
                              String title,
                              @Schema(description = "Task description", example = "Complete all API parts")
                              String description,
                              @Schema(description = "Task completion flag", example = "false")
                              Boolean completed,
                              @Schema(description = "Task creation timestamp", example = "2026-04-11T14:30:00")
                              LocalDateTime createdAt,
                              @Schema(description = "Task due date", example = "2026-05-01")
                              LocalDate dueDate,
                              @Schema(description = "Task priority")
                              Priority priority,
                              @Schema(description = "Task tags", example = "[\"study\",\"java\"]")
                              Set<String> tags) {
}
