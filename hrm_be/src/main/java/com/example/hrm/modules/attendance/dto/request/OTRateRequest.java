package com.example.hrm.modules.attendance.dto.request;

import com.example.hrm.shared.enums.OTType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OTRateRequest {

    @NotNull(message = "Ngày không được để trống")
    LocalDate date;

    @NotNull(message = "Loại tăng ca không được để trống")
    OTType type;

    @NotNull(message = "Tỷ lệ không được để trống")
    Double rate;

    String description;
}
