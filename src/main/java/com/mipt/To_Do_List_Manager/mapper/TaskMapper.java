package com.mipt.To_Do_List_Manager.mapper;

import com.mipt.To_Do_List_Manager.dto.TaskCreateDto;
import com.mipt.To_Do_List_Manager.dto.TaskResponseDto;
import com.mipt.To_Do_List_Manager.dto.TaskUpdateDto;
import com.mipt.To_Do_List_Manager.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

    TaskResponseDto toResponseDto(Task task);
}
