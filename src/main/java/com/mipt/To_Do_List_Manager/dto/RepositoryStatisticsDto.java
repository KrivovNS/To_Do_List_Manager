package com.mipt.To_Do_List_Manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RepositoryStatisticsDto", description = "Sizes of available task repositories")
public record RepositoryStatisticsDto(
        @Schema(description = "In-memory repository size", example = "10")
        int inMemoryTaskRepositorySize,
        @Schema(description = "Stub repository size", example = "5")
        int stubTaskRepositorySize
) {
}
