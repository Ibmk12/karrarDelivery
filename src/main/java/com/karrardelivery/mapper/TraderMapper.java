package com.karrardelivery.mapper;

import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Trader;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TraderMapper extends IBaseMapper<Trader, TraderDto> {


    @Mappings({
            @Mapping(target = "id", source = "id")
    })
    @Override
    TraderDto toDto(Trader trader);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapToUpdate(@MappingTarget Trader entity, TraderDto dto);
}
