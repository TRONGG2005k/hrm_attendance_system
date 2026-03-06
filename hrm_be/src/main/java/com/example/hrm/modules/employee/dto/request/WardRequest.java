package com.example.hrm.modules.employee.dto.request;

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
public class WardRequest {

    String id;

    @NotBlank(message = "Tên phường/xã không được để trống")
    String name;

    @NotBlank(message = "Quận/huyện không được để trống")
    String districtId;
}
