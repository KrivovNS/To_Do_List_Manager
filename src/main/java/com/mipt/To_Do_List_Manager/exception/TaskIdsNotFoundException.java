package com.mipt.To_Do_List_Manager.exception;

public class TaskIdsNotFoundException extends RuntimeException {

    private final Integer missingId;

    public TaskIdsNotFoundException(Integer missingId) {
        super("Task not found for id: " + missingId);
        this.missingId = missingId;
    }

    public Integer getMissingId() {
        return missingId;
    }
}


