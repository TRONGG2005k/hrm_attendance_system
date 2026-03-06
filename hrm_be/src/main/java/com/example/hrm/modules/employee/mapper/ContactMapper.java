package com.example.hrm.modules.employee.mapper;

import com.example.hrm.modules.employee.dto.request.ContactRequest;
import com.example.hrm.modules.employee.dto.response.ContactResponse;
import com.example.hrm.modules.employee.entity.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(target = "employeeId",  source = "employee.id")
    ContactResponse toResponse(Contact contact);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Contact toEntity(ContactRequest request);


}
