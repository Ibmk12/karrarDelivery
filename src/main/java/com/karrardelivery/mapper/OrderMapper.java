package com.karrardelivery.mapper;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.entity.Order;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {EEmirateMapper.class, EDeliveryStatusMapper.class})
public interface OrderMapper extends IBaseMapper<Order, OrderDto> {

    @Mappings({
            @Mapping(target = "emirate", source = "emirate", qualifiedByName = "emirateToString"),
            @Mapping(target = "deliveryStatus", source = "deliveryStatus", qualifiedByName = "deliveryStatusToString"),
            @Mapping(target = "traderId", source = "trader.id")
    })
    @Override
    OrderDto toDto(Order order);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "trader", ignore = true),
            @Mapping(target = "lastUpdated", ignore = true),
            @Mapping(target = "emirate", source = "emirate", qualifiedByName = "stringToEmirate"),
            @Mapping(target = "deliveryStatus", source = "deliveryStatus", qualifiedByName = "stringToDeliveryStatus")
    })
    @Override
    Order toEntity(OrderDto dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "emirate", source = "emirate", qualifiedByName = "stringToEmirate"),
            @Mapping(target = "deliveryStatus", source = "deliveryStatus", qualifiedByName = "stringToDeliveryStatus"),
            @Mapping(target = "trader", ignore = true),
            @Mapping(target = "lastUpdated", ignore = true)
    })
    void mapToUpdate(@MappingTarget Order entity, OrderDto dto);

    @Override
    default Page<OrderDto> mapToDtoPageable(Page<Order> pageOfEntity) {
        List<OrderDto> dtoList = pageOfEntity.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageOfEntity.getPageable(), pageOfEntity.getTotalElements());
    }
}
