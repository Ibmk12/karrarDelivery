package com.karrardelivery.mapper;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper extends IBaseMapper<Order, OrderDto> {

}
