package com.mipt.To_Do_List_Manager.model;

import java.util.Objects;

/**
 * Класс, реализующий сущность задания.
 * При создании считаем, что Task не выполнена, а id присваиваем при размещении в репозитории.
 */
public class Task {

    private int id;
    private String title;
    private String description;
    private boolean completed;

    /**
     * @param title - заголовок
     * @param description - описание
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public void setCompleted(boolean isCompleted) {
        this.completed = isCompleted;
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
