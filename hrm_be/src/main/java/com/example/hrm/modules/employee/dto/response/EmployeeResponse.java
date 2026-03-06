package com.example.hrm.modules.employee.dto.response;

import com.example.hrm.modules.organization.dto.response.PositionResponse;
import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import com.example.hrm.shared.enums.ShiftType;
import com.example.hrm.modules.file.dto.response.FileAttachmentResponse;
import com.example.hrm.modules.organization.dto.response.SubDepartmentResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeResponse {

    String id;
    String code;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    Gender gender;
    String email;
    String phone;

    List<FileAttachmentResponse> fileAttachmentResponses;
    List<ContactResponse> contacts;

    EmployeeStatus status;
    LocalDate joinDate;
    AddressResponse address;
    ShiftType shiftType;

    SubDepartmentResponse subDepartment;

    /** ✅ THÊM */
    PositionResponse position;

    Boolean isDeleted;
    LocalDateTime deletedAt;
}
