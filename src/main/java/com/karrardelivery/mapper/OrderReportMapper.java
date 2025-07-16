package com.karrardelivery.mapper;

import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.entity.Order;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {EEmirateMapper.class, EDeliveryStatusMapper.class})
public interface OrderReportMapper extends IBaseMapper<Order, OrderReportDto> {

    @Mappings({
            @Mapping(target = "emirate", source = "emirate", qualifiedByName = "emirateToString"),
            @Mapping(target = "deliveryStatus", source = "deliveryStatus", qualifiedByName = "deliveryStatusToString"),
            @Mapping(target = "traderName", source = "trader.name"),
            @Mapping(target = "traderCode", source = "trader.code"),
            @Mapping(target = "deliveryDate", source = "deliveryDate")
    })
    @Override
    OrderReportDto toDto(Order order);
}
