package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final HashMap<Integer, Task> taskRepository;

    public InMemoryTaskRepository(){
        taskRepository = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        int id = taskRepository.size() + 1;
        task.setId(id);

        taskRepository.put(id, task);
    }

    @Override
    public Task get(Integer id) {
        if (taskRepository.containsKey(id)) {
            return taskRepository.get(id);
        }

        return null;
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
