package com.mipt.To_Do_List_Manager.api;

import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskRequest;
import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskResponse;
import com.mipt.To_Do_List_Manager.service.TasksGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {

    private final TasksGatewayService tasksGatewayService;

    public TasksGatewayController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping
    public ResponseEntity<ExternalTaskResponse> create(@RequestBody @Valid ExternalTaskRequest request) {
        ExternalTaskResponse task = tasksGatewayService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(task.location())
                .body(task);
    }

    @GetMapping("/{id}")
    public ExternalTaskResponse get(@PathVariable long id) {
        return tasksGatewayService.get(id);
    }

    @GetMapping
    public List<ExternalTaskResponse> list(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit
    ) {
        return tasksGatewayService.list(completed, limit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        tasksGatewayService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ExternalTaskResponse unstable(@RequestParam(defaultValue = "500") String mode) {
        return tasksGatewayService.unstable(mode);
    }
}
