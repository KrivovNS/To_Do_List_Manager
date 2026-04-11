package com.mipt.To_Do_List_Manager.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Класс, реализующий сущность задания.
 * При создании считаем, что Task не выполнена, а id присваиваем при размещении в репозитории.
 */
@Getter
@Setter
public class Task {

    private int id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDate dueDate;
    private Priority priority;
    private Set<String> tags;

    /**
     * @param title - заголовок
     * @param description - описание
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Task other)) {
            return false;
        }

        return id == other.id
            && Objects.equals(title, other.title)
            && Objects.equals(description, other.description)
            && completed == other.completed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, completed);
    }

    @Override
    public String toString() {
        return "{Id=" + id + "\nTitle=" + title + "\nDescription:" + description + "\nCompleted="
            + completed + "}";
    }
}
