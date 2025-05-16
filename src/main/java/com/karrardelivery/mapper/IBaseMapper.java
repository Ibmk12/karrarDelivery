package com.karrardelivery.mapper;

import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import java.util.stream.Collectors;

public interface IBaseMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDtoList(List<E> entityList);

    List<E> toEntityList(List<D> dtoList);

    void mapToUpdate(@MappingTarget E entity, D dto);

    default Page<D> mapToDtoPageable(Page<E> pageOfEntity) {
        List<D> dtoList = pageOfEntity.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageOfEntity.getPageable(), pageOfEntity.getTotalElements());
    }

}