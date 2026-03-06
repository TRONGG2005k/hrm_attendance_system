package com.example.hrm.modules.organization.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubDepartmentRequest {

    @NotBlank(message = "Mã phòng ban không được để trống")
    String departmentId;

    @NotBlank(message = "Tên phòng ban con không được để trống")
    String name;

    String description;
}
