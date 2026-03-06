package com.example.hrm.modules.attendance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceOTRateResponse {

    String id;

    String attendanceId;

    String otRateId;

    Double otHours;

    LocalDateTime createdAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
