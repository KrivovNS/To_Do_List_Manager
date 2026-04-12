package com.mipt.To_Do_List_Manager.controller;

import com.mipt.To_Do_List_Manager.dto.ErrorResponse;
import com.mipt.To_Do_List_Manager.dto.TaskResponseDto;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Manage favorite tasks in user session")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PostMapping("/{taskId}")
    @Operation(summary = "Add task to favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task added to favorites"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> addToFavorites(@PathVariable Long taskId, HttpSession session) {
        favoritesService.addToFavorites(taskId, session);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Remove task from favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task removed from favorites")
    })
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long taskId, HttpSession session) {
        favoritesService.removeFromFavorites(taskId, session);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get favorite tasks for current session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite tasks returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class))))
    })
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<TaskResponseDto> favorites = favoritesService.getFavoriteTasks(session).stream()
                .map(this::toResponseDto)
                .toList();
        return ResponseEntity.ok(favorites);
    }

    private TaskResponseDto toResponseDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getDueDate(),
                task.getPriority(),
                task.getTags()
        );
    }
}
