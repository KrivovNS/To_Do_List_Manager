package com.mipt.To_Do_List_Manager.dto.gateway;

import java.util.List;

public record ProfileResponse(String username, List<String> authorities) {
}
