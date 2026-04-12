package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PreferencesController.class)
@Import(GlobalExceptionHandler.class)
class PreferencesControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getViewPreference_withoutCookie_setsDefault() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("detailed"))
                .andExpect(cookie().value("viewPreference", "detailed"));
    }

    @Test
    void setViewPreference_validMode() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("compact"))
                .andExpect(cookie().value("viewPreference", "compact"));
    }

    @Test
    void setViewPreference_invalidMode_returns400() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("mode must be one of")));
    }
}
