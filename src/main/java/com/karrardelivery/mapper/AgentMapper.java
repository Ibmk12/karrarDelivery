package com.karrardelivery.mapper;

import com.karrardelivery.dto.AgentDto;
import com.karrardelivery.entity.Agent;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AgentMapper extends IBaseMapper<Agent, AgentDto> {


    @Mappings({
            @Mapping(target = "id", source = "id")
    })
    @Override
    AgentDto toDto(Agent agent);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapToUpdate(@MappingTarget Agent entity, AgentDto dto);
}
