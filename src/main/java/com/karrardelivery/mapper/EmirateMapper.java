package com.karrardelivery.mapper;

import com.karrardelivery.dto.EmirateDto;
import com.karrardelivery.model.Emirate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmirateMapper extends IBaseMapper<Emirate, EmirateDto> {

}


