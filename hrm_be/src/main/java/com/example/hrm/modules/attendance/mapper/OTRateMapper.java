package com.example.hrm.modules.attendance.mapper;

import com.example.hrm.modules.attendance.dto.request.OTRateRequest;
import com.example.hrm.modules.attendance.dto.response.OTRateResponse;
import com.example.hrm.modules.attendance.entity.OTRate;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OTRateMapper {

    // Create
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OTRate toEntity(OTRateRequest request);

    // Update (KHÔNG tạo entity mới)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(OTRateRequest request, @MappingTarget OTRate entity);

    // Response
    OTRateResponse toResponse(OTRate entity);
}
