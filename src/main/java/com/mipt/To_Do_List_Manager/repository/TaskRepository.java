package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.Task;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория с основными операциями над заданиями: создание, получение(одного или всех),
 * обновление, удаление, проверка наличия.
 */
public interface TaskRepository {

    void add(Task task);

    Optional<Task> get(Integer id);

    List<Task> getAll();

    void update(Integer id, Task task);

    void delete(Integer id);

    boolean contains(Task task);
}
