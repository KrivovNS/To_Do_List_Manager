package com.mipt.To_Do_List_Manager.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Бин с областью видимости prototype.
 * Создается новый экземпляр при каждом обращении к контейнеру.
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {

    private final String uniqueId;

    public PrototypeScopedBean() {
        this.uniqueId = UUID.randomUUID().toString();
    }

    public String generateTaskId() {
        return "TASK-" + UUID.randomUUID().toString();
    }

    public String getUniqueId() {
        return uniqueId;
    }
}