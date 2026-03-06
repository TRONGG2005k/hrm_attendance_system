package com.example.hrm.modules.employee.mapper;

import com.example.hrm.modules.employee.dto.request.DistrictRequest;
import com.example.hrm.modules.employee.dto.response.DistrictResponse;
import com.example.hrm.modules.employee.entity.District;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProvinceMapper.class})
public interface DistrictMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "provinceId", target = "province", ignore = true)
    District toEntity(DistrictRequest request);

    @Mapping(source = "province", target = "province")
    DistrictResponse toResponse(District entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "provinceId", target = "province", ignore = true)
    void updateEntity(DistrictRequest request, @MappingTarget District entity);
}
