package com.karrardelivery.mapper;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TraderMapper extends IBaseMapper<Trader, TraderDto> {

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapToUpdate(@MappingTarget Trader entity, TraderDto dto);
}
