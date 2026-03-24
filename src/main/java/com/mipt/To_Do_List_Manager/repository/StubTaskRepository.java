package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.Task;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Репозиторий заданий с набором фиксированных данных, реализующий интерфейс TaskRepository.
 */
public class StubTaskRepository implements TaskRepository {

    private final Map<Integer, Task> taskRepository;

    /**
     * Конструктор класса с заполнением фиксированных данных.
     */
    public StubTaskRepository(){
        taskRepository = new ConcurrentHashMap<>();

        for (int i = 1; i <= 5; i++) {
            Task task =  new Task("Title" + i, "Description" + i);
            task.setId(i);

            taskRepository.put(i, task);
        }
    }

    /**
     * Добавляем задание в репозиторий.
     * Номер определяется по размеру репозитория.
     * @param task - добавляемое задание
     */
    @Override
    public void add(Task task) {
        int id = taskRepository.size() + 1;
        task.setId(id);

        taskRepository.put(id, task);
    }

    /**
     * Получение task по id
     * @param id - id искомого задания
     * @return - искомое задание или null, если по такому id нету task.
     */
    @Override
    public Optional<Task> get(Integer id) {
        if (taskRepository.containsKey(id)) {
            return Optional.of(taskRepository.get(id));
        }

        return Optional.empty();
    }

    /**
     * Обновление task по id.
     * @param id - id task, которую хотим обновить
     * @param task - новая task вместо старой
     */
    @Override
    public void update(Integer id, Task task) {
        if (taskRepository.containsKey(id)) {
            taskRepository.put(id, task);
        }
    }

    /**
     * Удаление по id.
     * @param id - id удаляемой task
     */
    @Override
    public void delete(Integer id) {
        taskRepository.remove(id);
    }

    /**
     * Проверяем наличие task в репозитории.
     * @param task - искомая task
     * @return булевое значение
     */
    @Override
    public boolean contains(Task task){
        Integer id = task.getId();

        if (taskRepository.containsKey(id)) {
            Task taskInRepository = taskRepository.get(id);
            return taskInRepository.equals(task);
        }

        return false;
    }

    /**
     * Получение всех заданий.
     * @return список всех заданий
     */
    @Override
    public ArrayList<Task> getAll() {
        return new ArrayList<>(taskRepository.values());
    }
}
