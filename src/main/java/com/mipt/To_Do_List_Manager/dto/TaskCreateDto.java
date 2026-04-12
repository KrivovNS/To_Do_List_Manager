package com.mipt.To_Do_List_Manager.dto;

import com.mipt.To_Do_List_Manager.marker.OnCreate;
import com.mipt.To_Do_List_Manager.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@Schema(name = "TaskCreateDto", description = "Payload for creating a new task")
public record TaskCreateDto(
        @Schema(description = "Task title", example = "Finish Spring homework")
        @NotBlank(groups = OnCreate.class)
        @Size(min=3, max=100, groups = OnCreate.class)
        String title,
        @Schema(description = "Task description", example = "Complete parts 1-6 before deadline")
        @Size(max=500, groups = OnCreate.class)
        String description,
        @Schema(description = "Task due date, must be today or in the future", example = "2026-05-01")
        @FutureOrPresent(groups = OnCreate.class)
        LocalDate dueDate,
        @Schema(description = "Task priority")
        @NotNull(groups = OnCreate.class)
        Priority priority,
        @Schema(description = "Set of tags, maximum 5 values", example = "[\"study\",\"backend\"]")
        @Size(max=5, groups = OnCreate.class)
        Set<String> tags) {
}
