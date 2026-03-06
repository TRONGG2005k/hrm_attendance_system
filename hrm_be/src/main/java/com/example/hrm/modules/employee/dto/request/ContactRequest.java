package com.example.hrm.modules.employee.dto.request;

import com.example.hrm.shared.enums.ContactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotNull(message = "Loại liên hệ không được để trống")
    ContactType type;

    @NotBlank(message = "Giá trị liên hệ không được để trống")
    String value;

    String relation;

    String note;
}
