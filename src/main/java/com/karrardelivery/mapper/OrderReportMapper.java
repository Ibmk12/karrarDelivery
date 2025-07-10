package com.karrardelivery.mapper;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.OrderReportDto;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {EEmirateMapper.class, EDeliveryStatusMapper.class})
public interface OrderReportMapper extends IBaseMapper<Order, OrderReportDto> {

    @Mappings({
            @Mapping(target = "emirate", source = "emirate", qualifiedByName = "emirateToString"),
            @Mapping(target = "deliveryStatus", source = "deliveryStatus", qualifiedByName = "deliveryStatusToString"),
            @Mapping(target = "traderName", source = "trader.name"),
            @Mapping(target = "traderCode", source = "trader.code")
    })
    @Override
    OrderReportDto toDto(Order order);
}
