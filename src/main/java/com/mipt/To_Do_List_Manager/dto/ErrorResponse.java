package com.mipt.To_Do_List_Manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(name = "ErrorResponse", description = "Standard error response returned by API")
public record ErrorResponse(
        @Schema(description = "Error timestamp in UTC", example = "2026-04-11T12:00:00Z")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Short HTTP status description", example = "Bad Request")
        String error,
        @Schema(description = "Human-readable message", example = "Validation failed")
        String message,
        @Schema(description = "Request path", example = "/api/tasks")
        String path,
        @Schema(description = "Additional error details")
        Map<String, Object> details
) {
}
