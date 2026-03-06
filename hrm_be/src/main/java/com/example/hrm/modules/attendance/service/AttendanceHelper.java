package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.BreakTime;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.ShiftType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceHelper {

    /**
     * Phân tích chấm công và ghi lại dữ liệu THUẦN vào Attendance
     * - lateMinutes  : số phút đi muộn thực tế
     * - earlyMinutes : số phút về sớm thực tế
     */
    public void analyzeAttendance(Attendance attendance) {
        if (attendance.getCheckInTime() == null
                || attendance.getCheckOutTime() == null) {
            return;
        }

        long lateMinutes = calculateLateMinutes(attendance);
        long earlyLeaveMinutes = calculateEarlyLeaveMinutes(attendance);

        attendance.setLateMinutes((int) lateMinutes);
        attendance.setEarlyLeaveMinutes((int) earlyLeaveMinutes);
    }

    /**
     * Tính thời điểm bắt đầu ca làm
     */
    public LocalDateTime getShiftStart(Employee employee, LocalDateTime checkInTime) {
        if (employee.getShiftType() == ShiftType.NIGHT) {
            LocalTime nightStart = LocalTime.of(22, 0);

            // check-in sau 0h sáng → ca đêm của ngày hôm trước
            if (checkInTime.toLocalTime().isBefore(nightStart)) {
                return checkInTime.minusDays(1)
                        .toLocalDate()
                        .atTime(nightStart);
            }

            return checkInTime.toLocalDate().atTime(nightStart);
        }

        // ca ngày
        return checkInTime.toLocalDate().atTime(8, 0);
    }

    /**
     * Tính thời điểm kết thúc ca làm
     */
    public LocalDateTime getShiftEnd(Employee employee, LocalDateTime checkInTime) {
        if (employee.getShiftType() == ShiftType.NIGHT) {
            return getShiftStart(employee, checkInTime).plusHours(8);
        }
        return checkInTime.toLocalDate().atTime(17, 0);
    }

    /**
     * Tính số phút đi muộn THỰC TẾ (không grace)
     */
    public long calculateLateMinutes(Attendance attendance) {
        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime shiftStart =
                getShiftStart(attendance.getEmployee(), checkIn);

        long minutes = Duration.between(shiftStart, checkIn).toMinutes();
        return Math.max(0, minutes);
    }

    /**
     * Tính số phút về sớm THỰC TẾ (không grace)
     */
    public long calculateEarlyLeaveMinutes(Attendance attendance) {
        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = attendance.getCheckOutTime();
        LocalDateTime shiftEnd =
                getShiftEnd(attendance.getEmployee(), checkIn);

        long minutes = Duration.between(checkOut, shiftEnd).toMinutes();
        return Math.max(0, minutes);
    }

    /**
     * Tính tổng số phút làm việc thực tế
     */
    public long calculateWorkedMinutes(Attendance attendance) {
        if (attendance.getCheckInTime() == null
                || attendance.getCheckOutTime() == null) {
            return 0;
        }

        return Duration.between(
                attendance.getCheckInTime(),
                attendance.getCheckOutTime()
        ).toMinutes();
    }

    /**
     * Tính tổng thời gian nghỉ trùng với OT
     */
    public long calculateBreakMinutesInOT(
            List<BreakTime> breaks,
            LocalDateTime shiftEnd,
            LocalDateTime checkOut
    ) {
        long totalMinutes = 0;

        for (BreakTime b : breaks) {
            LocalDateTime start = b.getBreakStart().isBefore(shiftEnd)
                    ? shiftEnd
                    : b.getBreakStart();

            LocalDateTime end = b.getBreakEnd().isAfter(checkOut)
                    ? checkOut
                    : b.getBreakEnd();

            if (start.isBefore(end)) {
                totalMinutes += Duration.between(start, end).toMinutes();
            }
        }

        return totalMinutes;
    }
}
