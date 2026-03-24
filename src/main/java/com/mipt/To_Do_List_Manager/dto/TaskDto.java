package com.mipt.To_Do_List_Manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class TaskDto {
    @NotBlank(message = "title обязателен")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "description обязателен")
    @JsonProperty("description")
    private String description;

    public TaskDto() {
    }

    public TaskDto(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
