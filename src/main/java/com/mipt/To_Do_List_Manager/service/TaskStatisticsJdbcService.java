package com.mipt.To_Do_List_Manager.service;

import com.mipt.To_Do_List_Manager.dto.PriorityCountDto;
import com.mipt.To_Do_List_Manager.model.Priority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class TaskStatisticsJdbcService {

    private static final String SQL = """
            SELECT priority, COUNT(*) AS tasks_count
            FROM tasks
            GROUP BY priority
            ORDER BY priority
            """;

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PriorityCountDto> getTasksCountByPriority() {
        return jdbcTemplate.query(SQL, new PriorityCountRowMapper());
    }

    private static class PriorityCountRowMapper implements RowMapper<PriorityCountDto> {
        @Override
        public PriorityCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Priority priority = Priority.valueOf(rs.getString("priority"));
            long count = rs.getLong("tasks_count");
            return new PriorityCountDto(priority, count);
        }
    }
}


