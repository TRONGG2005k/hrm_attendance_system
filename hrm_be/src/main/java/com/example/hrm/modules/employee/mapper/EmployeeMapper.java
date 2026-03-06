package com.example.hrm.modules.employee.mapper;

import com.example.hrm.modules.employee.dto.request.EmployeeRequest;
import com.example.hrm.modules.employee.dto.response.EmployeeResponse;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.file.mapper.FileAttachmentMapper;

import com.example.hrm.modules.organization.mapper.PositionMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                AddressMapper.class,
                SubDepartmentMapper.class,
                PositionMapper.class,
                FileAttachmentMapper.class
        }
)
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "files", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Employee toEntity(EmployeeRequest request);

    @Mapping(target = "fileAttachmentResponses", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    EmployeeResponse toResponse(Employee entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "address", ignore = true)
    void updateEntity(EmployeeRequest request, @MappingTarget Employee entity);
}
