package com.example.hrm.modules.employee.dto.request;

import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import com.example.hrm.shared.enums.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeRequest {

    @NotBlank
    String code;

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    LocalDate dateOfBirth;

    Gender gender;

    @Email
    String email;

    String phone;

    EmployeeStatus status;

    LocalDate joinDate;

    ShiftType shiftType;

    /** đổi tên */
    String addressId;

    String subDepartmentId;

    String positionId;
}


