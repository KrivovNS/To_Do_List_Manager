package com.mipt.To_Do_List_Manager.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskRequest;
import com.mipt.To_Do_List_Manager.dto.gateway.ExternalTaskResponse;
import com.mipt.To_Do_List_Manager.exception.ExternalApiException;
import com.mipt.To_Do_List_Manager.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final int MAX_LOGGED_BODY_LENGTH = 300;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient externalRestClient, ObjectMapper objectMapper) {
        this.restClient = externalRestClient;
        this.objectMapper = objectMapper;
    }

    public ExternalTaskResponse create(ExternalTaskRequest request) {
        return restClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((httpRequest, response) -> {
                    String body = readBody(response.getBody());
                    if (response.getStatusCode() != HttpStatus.CREATED) {
                        throw exceptionFor(response.getStatusCode().value(), response.getHeaders(), body);
                    }
                    ExternalTaskResponse task = parseJson(body, ExternalTaskResponse.class, response.getHeaders());
                    URI location = response.getHeaders().getLocation();
                    return new ExternalTaskResponse(
                            task.id(),
                            task.title(),
                            task.description(),
                            task.completed(),
                            location,
                            task.degradationReason()
                    );
                });
    }

    public ExternalTaskResponse get(long id) {
        return restClient.get()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    String body = readBody(response.getBody());
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw exceptionFor(response.getStatusCode().value(), response.getHeaders(), body);
                    }
                    return parseJson(body, ExternalTaskResponse.class, response.getHeaders());
                });
    }

    public List<ExternalTaskResponse> list(Boolean completed, Integer limit) {
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/tasks");
                    if (completed != null) {
                        builder.queryParam("completed", completed);
                    }
                    if (limit != null) {
                        builder.queryParam("limit", limit);
                    }
                    return builder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    String body = readBody(response.getBody());
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw exceptionFor(response.getStatusCode().value(), response.getHeaders(), body);
                    }
                    return parseJson(body, new ParameterizedTypeReference<List<ExternalTaskResponse>>() {
                    }, response.getHeaders());
                });
    }

    public void delete(long id) {
        restClient.delete()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value()
                                || status.is5xxServerError()
                                || status.value() == HttpStatus.TOO_MANY_REQUESTS.value(),
                        (request, response) -> {
                            String body = readBody(response.getBody());
                            throw exceptionFor(response.getStatusCode().value(), response.getHeaders(), body);
                        })
                .toBodilessEntity();
    }

    public ExternalTaskResponse unstable(String mode) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/unstable").queryParam("mode", mode).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    String body = readBody(response.getBody());
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw exceptionFor(response.getStatusCode().value(), response.getHeaders(), body);
                    }
                    return parseJson(body, ExternalTaskResponse.class, response.getHeaders());
                });
    }

    private RuntimeException exceptionFor(int status, HttpHeaders headers, String body) {
        if (status == HttpStatus.NOT_FOUND.value()) {
            throw new TaskNotFoundException(problemDetail(body));
        }
        if (status >= 500 || status == HttpStatus.TOO_MANY_REQUESTS.value()) {
            MediaType contentType = headers.getContentType();
            if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                log.warn("External API returned unexpected content type {} with body '{}'",
                        contentType, limited(body));
            }
            throw new ExternalApiException("External API failed with status " + status + ": " + problemDetail(body));
        }
        throw new ExternalApiException("External API returned unexpected status " + status);
    }

    private String problemDetail(String body) {
        try {
            JsonNode json = objectMapper.readTree(body);
            if (json.hasNonNull("detail")) {
                return json.get("detail").asText();
            }
            if (json.hasNonNull("title")) {
                return json.get("title").asText();
            }
        } catch (Exception ignored) {
            return limited(body);
        }
        return "External API error";
    }

    private <T> T parseJson(String body, Class<T> type, HttpHeaders headers) {
        try {
            validateJsonContentType(headers, body);
            return objectMapper.readValue(body, type);
        } catch (IOException ex) {
            throw new ExternalApiException("Could not parse external API response", ex);
        }
    }

    private <T> T parseJson(String body, ParameterizedTypeReference<T> type, HttpHeaders headers) {
        try {
            validateJsonContentType(headers, body);
            return objectMapper.readValue(body, objectMapper.getTypeFactory().constructType(type.getType()));
        } catch (IOException ex) {
            throw new ExternalApiException("Could not parse external API response", ex);
        }
    }

    private void validateJsonContentType(HttpHeaders headers, String body) {
        MediaType contentType = headers.getContentType();
        if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            log.warn("External API returned unexpected content type {} with body '{}'", contentType, limited(body));
            throw new ExternalApiException("External API returned non-JSON response");
        }
    }

    private String readBody(java.io.InputStream body) throws IOException {
        if (body == null) {
            return "";
        }
        return StreamUtils.copyToString(body, StandardCharsets.UTF_8);
    }

    private String limited(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= MAX_LOGGED_BODY_LENGTH ? body : body.substring(0, MAX_LOGGED_BODY_LENGTH);
    }
}
