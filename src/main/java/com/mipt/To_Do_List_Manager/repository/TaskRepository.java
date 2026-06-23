package com.mipt.To_Do_List_Manager.repository;

import com.mipt.To_Do_List_Manager.model.Priority;
import com.mipt.To_Do_List_Manager.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.dueDate IS NOT NULL
              AND t.dueDate BETWEEN :startDate AND :endDate
            """)
    List<Task> findTasksDueBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @EntityGraph(attributePaths = "attachments")
    @Query("SELECT t FROM Task t")
    List<Task> findAllWithAttachments();
}
