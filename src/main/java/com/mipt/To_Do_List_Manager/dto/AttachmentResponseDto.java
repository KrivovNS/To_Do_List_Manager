package com.mipt.To_Do_List_Manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "AttachmentResponseDto", description = "Attachment metadata returned by API")
public record AttachmentResponseDto(
        @Schema(description = "Attachment identifier", example = "1")
        Long id,
        @Schema(description = "Original file name", example = "requirements.pdf")
        String fileName,
        @Schema(description = "File size in bytes", example = "102400")
        long size,
        @Schema(description = "Upload timestamp", example = "2026-04-11T15:00:00")
        LocalDateTime uploadedAt
) {
}
