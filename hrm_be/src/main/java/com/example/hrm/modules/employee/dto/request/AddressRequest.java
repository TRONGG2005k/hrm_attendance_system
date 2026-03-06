package com.example.hrm.modules.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {

    String street;

    @NotBlank(message = "Phường/xã không được để trống")
    String wardId;

    String country;

    String note;
}
