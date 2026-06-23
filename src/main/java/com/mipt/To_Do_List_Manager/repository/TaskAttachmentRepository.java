package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    List<TaskAttachment> findByTask_Id(Integer taskId);
}
