package com.example.hrm.modules.organization.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import com.example.hrm.modules.employee.dto.response.EmployeeResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubDepartmentResponseDetail {

    String id;

    String departmentId;

    String name;

    String description;

    List<EmployeeResponse> employeeResponses;
}
