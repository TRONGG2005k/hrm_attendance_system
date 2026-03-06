package com.example.hrm.modules.attendance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceSummaryResponse {

    private long expectedWorkingDays;    // Tổng số ngày làm việc trong kỳ
    private long actualWorkingDays;      // Số ngày thực tế đi làm
//    private long absentDays;             // Số ngày nghỉ không phép
//    private long paidLeaveDays;          // Số ngày nghỉ có lương
    private long lateDays;               // Số ngày đi trễ
    private long earlyLeaveDays;         // Số ngày về sớm
    private long totalOtHours;           // Tổng số giờ OT
}
