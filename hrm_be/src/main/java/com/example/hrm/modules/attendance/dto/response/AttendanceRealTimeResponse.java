package com.example.hrm.modules.attendance.dto.response;

import com.example.hrm.shared.enums.AttendanceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceRealTimeResponse {

    String employeeCode;
    String employeeName;

    LocalDateTime time; // thời điểm quét (check-in / check-out)

    AttendanceStatus status;
    // WORKING / COMPLETED / LATE (tuỳ bạn định nghĩa)

    String message; // "Check-in thành công", "Đi trễ", "Check-out thành công"

}
