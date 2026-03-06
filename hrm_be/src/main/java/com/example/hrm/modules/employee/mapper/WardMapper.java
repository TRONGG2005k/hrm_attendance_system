package com.example.hrm.modules.employee.mapper;

import com.example.hrm.modules.employee.dto.request.WardRequest;
import com.example.hrm.modules.employee.dto.response.WardResponse;
import com.example.hrm.modules.employee.entity.Ward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {DistrictMapper.class})
public interface WardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "districtId", target = "district", ignore = true)
    Ward toEntity(WardRequest request);

    @Mapping(source = "district", target = "district")
    WardResponse toResponse(Ward entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "districtId", target = "district", ignore = true)
    void updateEntity(WardRequest request, @MappingTarget Ward entity);
}
