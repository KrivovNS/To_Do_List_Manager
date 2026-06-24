package com.mipt.To_Do_List_Manager.api;

import com.mipt.To_Do_List_Manager.dto.gateway.DocsResponse;
import com.mipt.To_Do_List_Manager.dto.gateway.ProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/profile")
    public ProfileResponse profile(Authentication authentication) {
        return new ProfileResponse(
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .sorted()
                        .toList()
        );
    }

    @GetMapping("/docs")
    public DocsResponse docs() {
        return new DocsResponse("Secure docs", "This endpoint requires READ_PRIVILEGE.");
    }
}
