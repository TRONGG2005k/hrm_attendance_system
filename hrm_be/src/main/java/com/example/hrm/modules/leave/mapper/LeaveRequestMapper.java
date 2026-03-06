package com.example.hrm.modules.leave.mapper;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.leave.dto.request.LeaveRequestCreateRequest;
import com.example.hrm.modules.leave.dto.response.LeaveRequestDetailResponse;
import com.example.hrm.modules.leave.dto.response.LeaveRequestListItemResponse;
import com.example.hrm.modules.leave.entity.LeaveRequest;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

    // Create request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    LeaveRequest toEntity(LeaveRequestCreateRequest request, Employee employee);

    // Entity -> List response
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeCode", source = "employee.code")
    @Mapping(target = "fullName", ignore = true)
    LeaveRequestListItemResponse toListItemResponse(LeaveRequest leaveRequest);

    // Entity -> Detail response
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeCode", source = "employee.code")
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "approvedById", source = "approvedBy.id")
    @Mapping(target = "approvedByName", ignore = true)
    LeaveRequestDetailResponse toDetailResponse(LeaveRequest leaveRequest);
}
