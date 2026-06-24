package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.client.ExternalTasksClient;
import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskRequest;
import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskResponse;
import com.mipt.To_Do_List_Manager.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createFallback")
    public ExternalTaskResponse create(ExternalTaskRequest request) {
        return externalTasksClient.create(request);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getFallback")
    public ExternalTaskResponse get(long id) {
        return externalTasksClient.get(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "listFallback")
    public List<ExternalTaskResponse> list(Boolean completed, Integer limit) {
        return externalTasksClient.list(completed, limit);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteFallback")
    public void delete(long id) {
        externalTasksClient.delete(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "unstableFallback")
    public ExternalTaskResponse unstable(String mode) {
        return externalTasksClient.unstable(mode);
    }

    ExternalTaskResponse createFallback(ExternalTaskRequest request, Throwable ex) {
        return ExternalTaskResponse.degraded(0, "External task creation is temporarily unavailable: " + ex.getClass().getSimpleName());
    }

    ExternalTaskResponse getFallback(long id, Throwable ex) {
        if (ex instanceof TaskNotFoundException taskNotFoundException) {
            throw taskNotFoundException;
        }
        return ExternalTaskResponse.degraded(id, "External task lookup is temporarily unavailable: " + ex.getClass().getSimpleName());
    }

    List<ExternalTaskResponse> listFallback(Boolean completed, Integer limit, Throwable ex) {
        return List.of(ExternalTaskResponse.degraded(0, "External task list is temporarily unavailable: " + ex.getClass().getSimpleName()));
    }

    void deleteFallback(long id, Throwable ex) {
        if (ex instanceof TaskNotFoundException taskNotFoundException) {
            throw taskNotFoundException;
        }
    }

    ExternalTaskResponse unstableFallback(String mode, Throwable ex) {
        return ExternalTaskResponse.degraded(0, "Unstable external call fell back after " + mode + ": " + ex.getClass().getSimpleName());
    }
}
