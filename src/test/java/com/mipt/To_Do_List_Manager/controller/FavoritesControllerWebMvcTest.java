package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.service.FavoritesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoritesController.class)
class FavoritesControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoritesService favoritesService;

    @Test
    void addToFavorites_success() throws Exception {
        mockMvc.perform(post("/api/favorites/{taskId}", 1L).session(new MockHttpSession()))
                .andExpect(status().isNoContent());
    }

    @Test
    void addToFavorites_notFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"))
                .when(favoritesService).addToFavorites(org.mockito.ArgumentMatchers.eq(99L), org.mockito.ArgumentMatchers.any());

        mockMvc.perform(post("/api/favorites/{taskId}", 99L).session(new MockHttpSession()))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeFromFavorites_success() throws Exception {
        mockMvc.perform(delete("/api/favorites/{taskId}", 1L).session(new MockHttpSession()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getFavorites_success() throws Exception {
        Task task = new Task("Favorite task", "Desc");
        task.setId(3);
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.of(2026, 4, 11, 12, 0));
        task.setDueDate(LocalDate.of(2026, 4, 20));
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("fav"));

        when(favoritesService.getFavoriteTasks(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(task));

        mockMvc.perform(get("/api/favorites").session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].title").value("Favorite task"));
    }
}
