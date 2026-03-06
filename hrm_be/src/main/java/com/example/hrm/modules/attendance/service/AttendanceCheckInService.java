package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.response.AttendanceRealTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.attendance.entity.OTRate;
import com.example.hrm.shared.enums.AttendanceEvaluation;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.enums.OTType;
import com.example.hrm.shared.enums.ShiftType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.attendance.repository.OTRateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceCheckInService {

    private final AttendanceRepository attendanceRepository;
    private final OTRateRepository otRateRepository;
    private final AttendanceHelper attendancePolicy;

    @Transactional
    public AttendanceRealTimeResponse checkIn(Employee employee) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime shiftStart = attendancePolicy.getShiftStart(employee, now);


        AttendanceEvaluation evaluation =
                now.isAfter(shiftStart)
                        ? AttendanceEvaluation.LATE
                        : AttendanceEvaluation.ON_TIME;

        LocalDate workDate = calculateWorkDate(employee, now);

        // Kiểm tra trùng lặp - đã có attendance cho employee + workDate chưa
        if (hasExistingAttendanceToday(employee, workDate)) {
            throw new AppException(ErrorCode.ATTENDANCE_DUPLICATE_DATE, 400,
                    "Nhân viên đã check-in cho ngày làm việc này");
        }

        Attendance attendance = Attendance.builder()
                .workDate(workDate)
                .employee(employee)
                .checkInTime(now)
                .status(AttendanceStatus.WORKING)
                .evaluation(evaluation)
                .isDeleted(false)
                .breaks(new ArrayList<>())
                .attendanceOTRates(new ArrayList<>())
                .build();

        DayOfWeek dayOfWeek = workDate.getDayOfWeek();
        boolean isSunday = dayOfWeek == DayOfWeek.SUNDAY;

        OTRate otRate;

        if (otRateRepository.existsByDateAndTypeAndIsDeletedFalse(workDate, OTType.HOLIDAY)) {
            otRate = otRateRepository
                    .findByDateAndTypeAndIsDeletedFalse(workDate, OTType.HOLIDAY)
                    .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        } else if (isSunday) {
            otRate = otRateRepository
                    .findByTypeAndIsDeletedFalse(OTType.SUNDAY)
                    .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        } else {
            otRate = otRateRepository
                    .findByTypeAndIsDeletedFalse(OTType.NORMAL)
                    .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));
        }

        AttendanceOTRate attendanceOTRate = new AttendanceOTRate();
        attendanceOTRate.setAttendance(attendance); // 🔥 BẮT BUỘC
        attendanceOTRate.setOtRate(otRate);
        attendanceOTRate.setOtHours(0.0);
        attendanceOTRate.setIsDeleted(false);

        attendance.getAttendanceOTRates().add(attendanceOTRate);

        attendanceRepository.save(attendance);

        return AttendanceRealTimeResponse.builder()
                .employeeCode(employee.getCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .time(now)
                .status(AttendanceStatus.WORKING)
                .message(
                        evaluation == AttendanceEvaluation.LATE
                                ? "Bạn đã đi trễ"
                                : "Check-in thành công"
                )
                .build();
    }

    /**
     * Tính workDate đúng cho cả ca ngày và ca đêm
     * Ca đêm: 22:00 - 06:00 hôm sau
     */
    private LocalDate calculateWorkDate(Employee employee, LocalDateTime now) {
        if (employee.getShiftType() == ShiftType.NIGHT) {
            // Ca đêm bắt đầu từ 22:00 và kết thúc 06:00 hôm sau
            // Nếu check-in trước 6h sáng → thuộc ca đêm của ngày hôm trước
            // Nếu check-in từ 22:00 trở đi → thuộc ca đêm của ngày hiện tại
            LocalTime time = now.toLocalTime();
            if (time.isBefore(LocalTime.of(6, 0))) {
                // Trước 6h sáng → thuộc ca đêm ngày hôm trước
                return now.minusDays(1).toLocalDate();
            } else if (time.isAfter(LocalTime.of(21, 59))) {
                // Sau 22:00 → thuộc ca đêm ngày hiện tại
                return now.toLocalDate();
            } else {
                // Giữa 6h-22h → không phải ca đêm, dùng ngày hiện tại
                return now.toLocalDate();
            }
        }
        // Ca ngày: dùng ngày hiện tại
        return now.toLocalDate();
    }

    /**
     * Kiểm tra xem đã có attendance cho employee và workDate chưa
     */
    private boolean hasExistingAttendanceToday(Employee employee, LocalDate workDate) {
        // Lấy attendance mới nhất của employee
        var existingAttendance = attendanceRepository
                .findTopByEmployeeOrderByCheckInTimeDesc(employee);

        if (existingAttendance.isPresent()) {
            Attendance attendance = existingAttendance.get();
            // Kiểm tra nếu attendance hiện tại đang WORKING (chưa check-out)
            if (attendance.getStatus() == AttendanceStatus.WORKING) {
                return true;
            }
            // Kiểm tra nếu đã có attendance cho workDate này
            if (attendance.getWorkDate() != null && attendance.getWorkDate().equals(workDate)) {
                return true;
            }
        }
        return false;
    }

}
