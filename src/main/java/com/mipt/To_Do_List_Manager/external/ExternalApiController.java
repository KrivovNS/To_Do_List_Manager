package com.mipt.To_Do_List_Manager.external;

import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskRequest;
import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, ExternalTaskResponse> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1000);

    @PostMapping("/tasks")
    public ResponseEntity<ExternalTaskResponse> create(@RequestBody @Valid ExternalTaskRequest request) {
        long id = idSequence.incrementAndGet();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        ExternalTaskResponse task = new ExternalTaskResponse(
                id,
                request.title(),
                request.description(),
                Boolean.TRUE.equals(request.completed()),
                location,
                null
        );
        tasks.put(id, task);
        return ResponseEntity.created(location).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        ExternalTaskResponse task = tasks.get(id);
        if (task == null) {
            return notFound(id);
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public List<ExternalTaskResponse> list(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit
    ) {
        return tasks.values().stream()
                .filter(task -> completed == null || task.completed() == completed)
                .sorted(Comparator.comparingLong(ExternalTaskResponse::id))
                .limit(limit == null ? Long.MAX_VALUE : Math.max(0, limit))
                .toList();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        ExternalTaskResponse removed = tasks.remove(id);
        if (removed == null) {
            return notFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam(defaultValue = "500") String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(5000);
                yield ResponseEntity.ok(new ExternalTaskResponse(0, "Late response", "timeout mode", false, null, null));
            }
            case "429" -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "5")
                    .body(problem(HttpStatus.TOO_MANY_REQUESTS, "Too many external requests"));
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>Bad Gateway</h1></body></html>");
            case "ok" -> ResponseEntity.ok(new ExternalTaskResponse(0, "Stable response", "ok mode", false, null, null));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(problem(HttpStatus.INTERNAL_SERVER_ERROR, "Synthetic external failure"));
        };
    }

    private ResponseEntity<ProblemDetail> notFound(long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problem(HttpStatus.NOT_FOUND, "External task " + id + " was not found"));
    }

    private ProblemDetail problem(HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(status.getReasonPhrase());
        return problemDetail;
    }
}
