package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.TaskAttachment;

import java.util.List;
import java.util.Optional;

public interface TaskAttachmentRepository {

    TaskAttachment save(TaskAttachment attachment);

    Optional<TaskAttachment> findById(Long id);

    List<TaskAttachment> findByTaskId(Long taskId);

    void deleteById(Long id);
}
