package com.mipt.To_Do_List_Manager.validation;

import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    private final TaskService taskService;
    private final HttpServletRequest request;

    public DueDateNotBeforeCreationValidator(TaskService taskService, HttpServletRequest request) {
        this.taskService = taskService;
        this.request = request;
    }

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.dueDate() == null) {
            return true;
        }

        Integer id = extractIdFromPath();
        if (id == null) {
            return true;
        }

        var taskOpt = taskService.getTaskById(id);
        if (taskOpt.isEmpty() || taskOpt.get().getCreatedAt() == null) {
            return true;
        }

        return !dto.dueDate().isBefore(taskOpt.get().getCreatedAt().toLocalDate());
    }

    private Integer extractIdFromPath() {
        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (!(attr instanceof Map<?, ?> pathVariables)) {
            return null;
        }

        Object rawId = pathVariables.get("id");
        if (rawId == null) {
            return null;
        }

        try {
            return Integer.valueOf(rawId.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
