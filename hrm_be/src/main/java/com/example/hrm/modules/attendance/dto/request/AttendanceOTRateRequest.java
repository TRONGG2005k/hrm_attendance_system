package com.example.hrm.modules.attendance.dto.request;

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
public class AttendanceOTRateRequest {

    @NotBlank(message = "Mã điểm danh không được để trống")
    String attendanceId;

    @NotBlank(message = "Mã tỷ lệ tăng ca không được để trống")
    String otRateId;

    @NotNull(message = "Số giờ tăng ca không được để trống")
    Double otHours;
}
