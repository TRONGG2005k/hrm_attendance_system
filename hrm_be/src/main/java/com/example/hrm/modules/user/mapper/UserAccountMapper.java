package com.example.hrm.modules.user.mapper;

import com.example.hrm.modules.user.dto.request.UserAccountRequest;
import com.example.hrm.modules.user.dto.response.UserAccountResponse;
import com.example.hrm.modules.user.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserAccount toEntity(UserAccountRequest request);

    @Mapping(target = "employeeCode", source = "employee.code")
    @Mapping(target = "employeeId", source = "employee.id")
    UserAccountResponse toResponse(UserAccount userAccount);


}
