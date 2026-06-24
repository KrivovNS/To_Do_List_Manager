package com.mipt.To_Do_List_Manager.dto.gateway;

import java.net.URI;

public record ExternalTaskResponse(
        long id,
        String title,
        String description,
        boolean completed,
        URI location,
        String degradationReason
) {
    public static ExternalTaskResponse degraded(long id, String reason) {
        return new ExternalTaskResponse(id, "Temporarily unavailable", reason, false, null, reason);
    }
}
