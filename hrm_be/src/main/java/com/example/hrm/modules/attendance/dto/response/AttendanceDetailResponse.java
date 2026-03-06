package com.example.hrm.modules.attendance.dto.response;

import com.example.hrm.shared.enums.OTType;
import com.example.hrm.shared.enums.ShiftType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDetailResponse {

    /* ===================== EMPLOYEE ===================== */

    private String employeeId;
    private String employeeCode;
    private String employeeName;
    private String subDepartmentName;

    /* ===================== WORK DATE ===================== */

    private LocalDate workDate;

    /* ===================== SHIFT ===================== */

    private ShiftType shiftType;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;

    /* ===================== ATTENDANCE TIME ===================== */

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    /* ===================== CALCULATED ===================== */

    /** Số phút đi trễ */
    private long lateMinutes;

    /** Số phút về sớm */
    private long earlyLeaveMinutes;

    /** Tổng phút làm việc thực tế (đã trừ break) */
    private long workedMinutes;

    /** Tổng giờ làm việc (hiển thị) */
    private double workedHours;

    /* ===================== BREAK ===================== */

    private List<BreakTimeResponse> breakTimes;

    /* ===================== OT ===================== */

    private long otMinutes;
    private double otHours;
    private OTType otType;
    private double otRate;

    /* ===================== STATUS ===================== */

    private Object status;
}
