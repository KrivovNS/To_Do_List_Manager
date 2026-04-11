package com.mipt.To_Do_List_Manager.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskAttachment {
    private Long id;
    private Long taskId;
    private String fileName;
    private String storedFileName;
    private String contentType;
    private long size;
    private LocalDateTime uploadedAt;


}
