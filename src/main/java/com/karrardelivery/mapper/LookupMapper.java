//package com.karrardelivery.mapper;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Mappings;
//
//@Mapper(componentModel = "spring")
//public interface LookupMapper extends IBaseMapper<LookupEntity, LookupDTO> {
//
//    @Mappings({
//            @Mapping(source = "code", target = "lookupKey"),
//            @Mapping(source = "descAr", target = "labelAr"),
//            @Mapping(source = "descEn", target = "labelEn"),
//            @Mapping(source = "type.code", target = "lookupType")
//    })
//    @Override
//    LookupDTO toDto(LookupEntity entity);
//
//}
//
//
