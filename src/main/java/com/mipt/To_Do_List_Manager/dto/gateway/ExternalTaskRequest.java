package com.mipt.To_Do_List_Manager.dto.gateway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExternalTaskRequest(
        @NotBlank
        @Size(max = 100)
        String title,
        @Size(max = 500)
        String description,
        Boolean completed
) {
}
