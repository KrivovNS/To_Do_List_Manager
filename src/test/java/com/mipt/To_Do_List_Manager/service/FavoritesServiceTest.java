package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoritesServiceTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private FavoritesService favoritesService;

    @Test
    void addToFavorites_taskExists() {
        MockHttpSession session = new MockHttpSession();
        Task task = new Task("Title", "Description");
        task.setId(1);
        when(taskService.getTaskById(1)).thenReturn(Optional.of(task));

        favoritesService.addToFavorites(1L, session);

        @SuppressWarnings("unchecked")
        Set<Integer> ids = (Set<Integer>) session.getAttribute(FavoritesService.FAVORITE_TASK_IDS_SESSION_ATTRIBUTE);
        assertEquals(Set.of(1), ids);
    }

    @Test
    void addToFavorites_taskNotFound() {
        MockHttpSession session = new MockHttpSession();
        when(taskService.getTaskById(42)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> favoritesService.addToFavorites(42L, session)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getFavoriteTasks_returnsExistingOnly() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(FavoritesService.FAVORITE_TASK_IDS_SESSION_ATTRIBUTE, Set.of(1, 2));

        Task existing = new Task("Existing", "Task");
        existing.setId(1);
        when(taskService.getTaskById(1)).thenReturn(Optional.of(existing));
        when(taskService.getTaskById(2)).thenReturn(Optional.empty());

        List<Task> result = favoritesService.getFavoriteTasks(session);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }
}
