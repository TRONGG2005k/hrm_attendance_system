package com.example.hrm.modules.employee.mapper;

import com.example.hrm.modules.employee.dto.request.ProvinceRequest;
import com.example.hrm.modules.employee.dto.response.ProvinceResponse;
import com.example.hrm.modules.employee.entity.Province;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Province toEntity(ProvinceRequest request);

    ProvinceResponse toResponse(Province entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProvinceRequest request, @MappingTarget Province entity);
}
