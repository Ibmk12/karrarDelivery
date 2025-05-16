package com.karrardelivery.mapper;

import com.karrardelivery.dto.EmirateDto;
import com.karrardelivery.entity.Emirate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmirateMapper extends IBaseMapper<Emirate, EmirateDto> {

}


