package com.example.hrm.modules.user.mapper;

import com.example.hrm.modules.user.dto.request.RoleRequest;
import com.example.hrm.modules.user.dto.response.RoleResponse;
import com.example.hrm.modules.user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Role toEntity(RoleRequest request);

    RoleResponse toResponse(Role role);
}
