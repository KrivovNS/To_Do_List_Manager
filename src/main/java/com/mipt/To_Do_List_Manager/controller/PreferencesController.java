package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "User preferences stored in cookies")
public class PreferencesController {

    private static final String VIEW_PREFERENCE_COOKIE_NAME = "viewPreference";
    private static final String DEFAULT_VIEW_MODE = "detailed";
    private static final Set<String> SUPPORTED_VIEW_MODES = Set.of("compact", "detailed");
    private static final int COOKIE_TTL_SECONDS = 60 * 60 * 24 * 30;

    @GetMapping("/view")
    @Operation(summary = "Get current view preference from cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current preference returned")
    })
    public ResponseEntity<Map<String, String>> getViewPreference(
            @CookieValue(value = VIEW_PREFERENCE_COOKIE_NAME, required = false) String viewModeFromCookie,
            HttpServletResponse response
    ) {
        String mode = normalizeOrDefault(viewModeFromCookie);

        if (viewModeFromCookie == null || !viewModeFromCookie.equalsIgnoreCase(mode)) {
            addViewPreferenceCookie(response, mode);
        }

        return ResponseEntity.ok(Map.of("mode", mode));
    }

    @PostMapping("/view")
    @Operation(summary = "Update view preference cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preference updated"),
            @ApiResponse(responseCode = "400", description = "Unsupported mode",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, String>> setViewPreference(
            @RequestParam("mode") String mode,
            HttpServletResponse response
    ) {
        String normalizedMode = normalize(mode);
        if (!SUPPORTED_VIEW_MODES.contains(normalizedMode)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "mode must be one of: compact, detailed"
            );
        }

        addViewPreferenceCookie(response, normalizedMode);
        return ResponseEntity.ok(Map.of("mode", normalizedMode));
    }

    private String normalizeOrDefault(String mode) {
        if (mode == null || mode.isBlank()) {
            return DEFAULT_VIEW_MODE;
        }

        String normalizedMode = normalize(mode);
        if (SUPPORTED_VIEW_MODES.contains(normalizedMode)) {
            return normalizedMode;
        }

        return DEFAULT_VIEW_MODE;
    }

    private String normalize(String mode) {
        return mode.trim().toLowerCase(Locale.ROOT);
    }

    private void addViewPreferenceCookie(HttpServletResponse response, String mode) {
        Cookie cookie = new Cookie(VIEW_PREFERENCE_COOKIE_NAME, mode);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_TTL_SECONDS);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }
}
