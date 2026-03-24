package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.dto.RepositoryStatisticsDto;
import com.mipt.To_Do_List_Manager.model.Task;
import com.mipt.To_Do_List_Manager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для демонстрации работы @Qualifier.
 * Инжектирует оба репозитория для сравнения.
 */
@Service
public class TaskStatisticsService {

    private final TaskRepository inMemoryTaskRepository;
    private final TaskRepository stubTaskRepository;

    @Autowired
    public TaskStatisticsService(TaskRepository inMemoryTaskRepository,
                                 @Qualifier("stubTaskRepository") TaskRepository stubTaskRepository) {
        this.inMemoryTaskRepository = inMemoryTaskRepository;
        this.stubTaskRepository = stubTaskRepository;
    }

    /**
     * Метод, определяющий репозиторий, в котором лежит задание.
     *
     * @param task - задание
     * @return - тип репозитория или null, если нет ни в одном из репозиториев
     */
    public Optional<TaskRepository> determineRepository(Task task) {
        if (inMemoryTaskRepository.contains(task)) {
            return Optional.of(inMemoryTaskRepository);
        }

        if (stubTaskRepository.contains(task)) {
            return Optional.of(stubTaskRepository);
        }

        return Optional.empty();
    }

    /**
     * Статистика обоих репозиториев.
     */
    public RepositoryStatisticsDto printStatistics() {
        return new RepositoryStatisticsDto(inMemoryTaskRepository.getAll().size(), stubTaskRepository.getAll().size());
    }
}
