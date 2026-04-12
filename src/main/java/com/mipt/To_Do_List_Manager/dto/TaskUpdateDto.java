package com.mipt.To_Do_List_Manager.dto;

import com.mipt.To_Do_List_Manager.marker.OnUpdate;
import com.mipt.To_Do_List_Manager.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@Schema(name = "TaskUpdateDto", description = "Payload for partial task update")
public record TaskUpdateDto(
        @Schema(description = "Updated task title", example = "Finish Spring project")
        @Size(min=3, max=100, groups = OnUpdate.class)
        String title,
        @Schema(description = "Updated task description", example = "Include validation and OpenAPI")
        @Size(max=500, groups = OnUpdate.class)
        String description,
        @Schema(description = "Updated completion status", example = "false")
        Boolean completed,
        @Schema(description = "Updated due date", example = "2026-05-10")
        LocalDate dueDate,
        @Schema(description = "Updated task priority")
        Priority priority,
        @Schema(description = "Updated tags, maximum 5 values", example = "[\"spring\",\"api\"]")
        @Size(max=5, groups = OnUpdate.class)
        Set<String> tags) {
}
