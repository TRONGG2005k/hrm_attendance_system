package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.response.AttendanceRealTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.modules.attendance.entity.BreakTime;
import com.example.hrm.shared.enums.AttendanceEvaluation;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.penalty.service.AttendancePenaltyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceCheckOutService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceHelper attendancePolicy;
    private final AttendancePenaltyService attendancePenaltyService;
    private final AttendanceHelper attendanceHelper;
    public AttendanceRealTimeResponse checkOut(Attendance attendance) {

        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = LocalDateTime.now();

        attendance.setCheckOutTime(checkOut);

        LocalDateTime shiftEnd = attendancePolicy.getShiftEnd(attendance.getEmployee(), checkIn);

        long rawOtMinutes = Math.max(
                0,
                Duration.between(shiftEnd, checkOut).toMinutes()
        );

        long breakOtMinutes = attendancePolicy.calculateBreakMinutesInOT(
                attendance.getBreaks(),
                shiftEnd,
                checkOut
        );

        long actualOtMinutes = Math.max(0, rawOtMinutes - breakOtMinutes);

        double otHours = actualOtMinutes >= 120
                ? actualOtMinutes / 60.0
                : 0.0;

        AttendanceOTRate otRate = attendance.getAttendanceOTRates()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 500));

        otRate.setOtHours(otHours);

        long workedMinutes = Duration.between(checkIn, checkOut).toMinutes();

        // Trừ thởi gian nghỉ trưa (mặc định 60 phút nếu làm đủ buổi sáng)
        long breakMinutes = calculateTotalBreakMinutes(attendance.getBreaks());
        long actualWorkedMinutes = Math.max(0, workedMinutes - breakMinutes);

        // 8 giờ = 480 phút (không tính nghỉ trưa)
        if (actualWorkedMinutes < 8 * 60) {
            attendance.setEvaluation(AttendanceEvaluation.LEAVE_EARLY);
        } else if (otHours > 0) {
            attendance.setEvaluation(AttendanceEvaluation.OVER_TIME);
        } else {
            attendance.setEvaluation(AttendanceEvaluation.ON_TIME);
        }

        attendanceHelper.analyzeAttendance(attendance);
        attendance.setStatus(AttendanceStatus.COMPLETED);
        attendanceRepository.save(attendance);
        attendancePenaltyService.calculateAndSave(attendance);

        return AttendanceRealTimeResponse.builder()
                .employeeCode(attendance.getEmployee().getCode())
                .employeeName(
                        attendance.getEmployee().getFirstName()
                                + " " + attendance.getEmployee().getLastName()
                )
                .time(checkOut)
                .status(attendance.getStatus())
                .message("Check-out thành công")
                .build();
    }

    /**
     * Tính tổng thởi gian nghỉ (break time)
     */
    private long calculateTotalBreakMinutes(List<BreakTime> breaks) {
        if (breaks == null || breaks.isEmpty()) {
            return 0;
        }
        long totalMinutes = 0;
        for (BreakTime b : breaks) {
            if (b.getBreakStart() != null && b.getBreakEnd() != null) {
                totalMinutes += Duration.between(b.getBreakStart(), b.getBreakEnd()).toMinutes();
            }
        }
        return totalMinutes;
    }
}
