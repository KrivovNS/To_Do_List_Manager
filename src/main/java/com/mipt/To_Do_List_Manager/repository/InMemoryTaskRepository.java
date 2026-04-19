package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.Task;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Integer, Task> taskRepository;
    private final AtomicInteger idSequence;

    public InMemoryTaskRepository(){
        taskRepository = new ConcurrentHashMap<>();
        idSequence = new AtomicInteger(0);
    }

    @Override
    public void add(Task task) {
        int id = idSequence.incrementAndGet();
        task.setId(id);

        taskRepository.put(id, task);
    }

    @Override
    public Optional<Task> get(Integer id) {
        if (taskRepository.containsKey(id)) {
            return Optional.of(taskRepository.get(id));
        }

        return Optional.empty();
    }

    @Override
    public void update(Integer id, Task task) {
        if (taskRepository.containsKey(id)) {
            taskRepository.put(id, task);
        }
    }

    @Override
    public void delete(Integer id) {
        taskRepository.remove(id);
    }

    @Override
    public boolean contains(Task task){
        Integer id = task.getId();

        if (taskRepository.containsKey(id)) {
            Task taskInRepository = taskRepository.get(id);
            return taskInRepository.equals(task);
        }

        return false;
    }

    @Override
    public ArrayList<Task> getAll() {
        return new ArrayList<>(taskRepository.values());
    }
}
