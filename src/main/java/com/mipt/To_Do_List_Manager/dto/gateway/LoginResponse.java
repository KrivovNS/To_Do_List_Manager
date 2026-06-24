package com.mipt.To_Do_List_Manager.dto.gateway;

import java.time.Instant;

public record LoginResponse(String accessToken, String tokenType, Instant expiresAt) {
}
