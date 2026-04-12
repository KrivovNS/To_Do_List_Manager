package com.mipt.To_Do_List_Manager.mapper;

import com.mipt.To_Do_List_Manager.dto.TaskCreateDto;
import com.mipt.To_Do_List_Manager.dto.TaskResponseDto;
import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.model.Task;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.LinkedHashSet;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tags", ignore = true)
    void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

    @AfterMapping
    default void updateTags(TaskUpdateDto dto, @MappingTarget Task task) {
        if (dto.tags() != null) {
            task.setTags(new LinkedHashSet<>(dto.tags()));
        }
    }

    TaskResponseDto toResponseDto(Task task);
}
