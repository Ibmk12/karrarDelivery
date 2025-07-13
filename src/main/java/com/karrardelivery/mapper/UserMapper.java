package com.karrardelivery.mapper;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.dto.UserDto;
import com.karrardelivery.entity.Order;
import com.karrardelivery.entity.Trader;
import com.karrardelivery.entity.management.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper extends IBaseMapper<User, UserDto> {


    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "id", source = "id")
    })
    @Override
    UserDto toDto(User user);

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    @Override
    User toEntity(UserDto dto);

    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "phone", ignore = true),
            @Mapping(target = "role", ignore = true),
            @Mapping(target = "enabled", ignore = true),
            @Mapping(target = "deleted", ignore = true)

    })
    void mapToUpdate(@MappingTarget User entity, UserDto dto);
}
