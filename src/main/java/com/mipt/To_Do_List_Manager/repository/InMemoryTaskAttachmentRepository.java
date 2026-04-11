package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

    private final ConcurrentHashMap<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(sequence.incrementAndGet());
        }

        storage.put(attachment.getId(), attachment);
        return attachment;
    }

    @Override
    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TaskAttachment> findByTaskId(Long taskId) {
        List<TaskAttachment> attachments = new ArrayList<>();

        for (TaskAttachment attachment : storage.values()) {
            if (taskId.equals(attachment.getTaskId())) {
                attachments.add(attachment);
            }
        }

        return attachments;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
