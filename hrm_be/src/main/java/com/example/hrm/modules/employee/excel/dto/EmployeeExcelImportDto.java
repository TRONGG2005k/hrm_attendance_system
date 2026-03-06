package com.example.hrm.modules.employee.excel.dto;

import com.example.hrm.shared.enums.ShiftType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeExcelImportDto {

    // Thông tin cơ bản
    private String code;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;          // MALE / FEMALE / OTHER
    private String email;
    private String phone;
    private String status;          // ACTIVE / INACTIVE
    private LocalDate joinDate;
    private ShiftType shiftType;
    // Địa chỉ
    private String street;
    private String ward;
    private String district;
    private String province;

    // Tổ chức
    private String departmentName;  // Tên phòng ban
    private String positionName;    // Tên chức vụ
}
