package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.response.AttendanceDetailResponse;
import com.example.hrm.modules.attendance.dto.response.AttendanceListResponse;
import com.example.hrm.modules.attendance.dto.response.BreakTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
// import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.leave.entity.LeaveBalance;
import com.example.hrm.modules.leave.entity.LeaveRequest;
import com.example.hrm.modules.leave.repository.LeaveBalanceRepository;
import com.example.hrm.shared.enums.AttendanceEvaluation;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.enums.LeaveType;
import com.example.hrm.shared.enums.OTType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {


    private final AttendanceRepository attendanceRepository;
    private final AttendanceHelper attendanceHelper;
//     private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<AttendanceListResponse> getAll(int page, int size) {
        return attendanceRepository
                .findByIsDeletedFalse(PageRequest.of(page, size))
                .map(this::mapToListResponse);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<AttendanceListResponse> getAllBySubDepartment(
            int page, int size, String subDepartmentId
    ) {
        return attendanceRepository
                .findBySubDepartmentId(PageRequest.of(page, size), subDepartmentId)
                .map(this::mapToListResponse);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public AttendanceDetailResponse getDetail(String attendanceId) {

        Attendance attendance = attendanceRepository
                .findByIdAndIsDeletedFalse(attendanceId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.ATTENDANCE_NOT_FOUND, 404)
                );

        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = attendance.getCheckOutTime();
        var employee = attendance.getEmployee();

        LocalDateTime shiftStart =
                attendanceHelper.getShiftStart(employee, checkIn);
        LocalDateTime shiftEnd =
                attendanceHelper.getShiftEnd(employee, checkIn);

        long lateMinutes = attendanceHelper
                .calculateLateMinutes(attendance);

        long earlyLeaveMinutes = 0;
        if (checkOut != null) {
            earlyLeaveMinutes = attendanceHelper
                    .calculateEarlyLeaveMinutes(attendance);
        }

        long workedMinutes = 0;
        double workedHours = 0;
        if (checkIn != null && checkOut != null) {
            workedMinutes = Duration.between(checkIn, checkOut).toMinutes();
            workedHours = workedMinutes / 60.0;
        }

        long otMinutes = 0;
        if (checkOut != null) {
            otMinutes = attendanceHelper.calculateBreakMinutesInOT(
                    attendance.getBreaks(),
                    shiftEnd,
                    checkOut
            );
        }

        var status = resolveStatus(
                checkOut,
                lateMinutes,
                earlyLeaveMinutes,
                otMinutes
        );

        // Cache OT rate data để tránh gọi getFirst() nhiều lần
        final double otRate;
        final OTType otType;
        if (attendance.getAttendanceOTRates() == null || attendance.getAttendanceOTRates().isEmpty()) {
            otRate = 0;
            otType = null;
        } else {
            var firstOtRate = attendance.getAttendanceOTRates().getFirst();
            if (firstOtRate != null && firstOtRate.getOtRate() != null) {
                otRate = firstOtRate.getOtRate().getRate();
                otType = firstOtRate.getOtRate().getType();
            } else {
                otRate = 0;
                otType = null;
            }
        }

        var breakResponses = attendance.getBreaks()
                .stream()
                .map(b -> BreakTimeResponse.builder()
                        .id(b.getId())
                        .breakStart(b.getBreakStart())
                        .breakEnd(b.getBreakEnd())
                        .type(b.getType())
                        .build()
                )
                .toList();

        return AttendanceDetailResponse.builder()
                .employeeId(employee.getId())
                .employeeCode(employee.getCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .subDepartmentName(employee.getSubDepartment().getName())

                .workDate(attendance.getWorkDate())

                .shiftType(employee.getShiftType())
                .shiftStart(shiftStart)
                .shiftEnd(shiftEnd)

                .checkInTime(checkIn)
                .checkOutTime(checkOut)

                .lateMinutes(lateMinutes)
                .earlyLeaveMinutes(earlyLeaveMinutes)

                .workedMinutes(workedMinutes)
                .workedHours(workedHours)

                .breakTimes(breakResponses)

                .otMinutes(otMinutes)
                .otHours(otMinutes / 60.0)
                .otRate(otRate)
                .otType(otType)

                .status(attendance.getStatus())
                .build();
    }

    /* ===================== MAPPER ===================== */

    private AttendanceListResponse mapToListResponse(Attendance attendance) {

        if (attendance.getStatus() == AttendanceStatus.LEAVE
                || attendance.getStatus() == AttendanceStatus.LEAVE_UNPAID) {

            return AttendanceListResponse.builder()
                    .id(attendance.getId())
                    .employeeCode(attendance.getEmployee().getCode())
                    .checkInTime(null)
                    .checkOutTime(null)
                    .lateMinutes(0)
                    .earlyLeaveMinutes(0)
                    .evaluation(attendance.getEvaluation())
                    .otMinutes(0)
                    .otRate(0)
                    .workDate(attendance.getWorkDate())
                    .status(attendance.getStatus())
                    .build();
        }
        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = attendance.getCheckOutTime();

        LocalDateTime shiftEnd =
                attendanceHelper.getShiftEnd(attendance.getEmployee(), checkIn);

        long lateMinutes =
                attendanceHelper.calculateLateMinutes(attendance);

        long earlyLeaveMinutes = 0;
        long otMinutes = 0;

        if (checkOut != null) {
            earlyLeaveMinutes =
                    attendanceHelper.calculateEarlyLeaveMinutes(attendance);

            otMinutes = attendanceHelper.calculateBreakMinutesInOT(
                    attendance.getBreaks(),
                    shiftEnd,
                    checkOut
            );
        }

        Object status = resolveStatus(
                checkOut,
                lateMinutes,
                earlyLeaveMinutes,
                otMinutes
        );

        // Cache OT rate để tránh gọi getFirst() nhiều lần và xử lý null
        final double otRate;
        if (attendance.getAttendanceOTRates() == null || attendance.getAttendanceOTRates().isEmpty()) {
            otRate = 1.0;
        } else {
            var firstOtRate = attendance.getAttendanceOTRates().getFirst();
            otRate = (firstOtRate != null && firstOtRate.getOtRate() != null)
                    ? firstOtRate.getOtRate().getRate()
                    : 1.0;
        }

        return AttendanceListResponse.builder()
                .id(attendance.getId())
                .employeeCode(attendance.getEmployee().getCode())
                .checkInTime(checkIn)
                .checkOutTime(checkOut)
                .lateMinutes(lateMinutes)
                .earlyLeaveMinutes(earlyLeaveMinutes)
                .otMinutes(otMinutes)
                .otRate(otRate)
                .evaluation(attendance.getEvaluation())
                .workDate(attendance.getWorkDate())
                .status(attendance.getStatus())
                .build();
    }

    /* ===================== STATUS ===================== */

    private Object resolveStatus(
            LocalDateTime checkOut,
            long lateMinutes,
            long earlyLeaveMinutes,
            long otMinutes
    ) {
        if (checkOut == null) {
            return AttendanceStatus.WORKING;
        }
        if (earlyLeaveMinutes > 0) {
            return AttendanceEvaluation.LEAVE_EARLY;
        }
        if (lateMinutes > 0) {
            return AttendanceEvaluation.LATE;
        }
        if (otMinutes > 0) {
            return AttendanceEvaluation.OVER_TIME;
        }
        return AttendanceEvaluation.ON_TIME;
    }

    public void generateFromLeave(LeaveRequest leave) {

        LocalDate current = leave.getStartDate();
        LeaveBalance leaveBalance = leaveBalanceRepository
                .findByEmployeeAndYearAndIsDeletedFalse(
                        leave.getEmployee(), leave.getStartDate().getYear())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404, "Không tìm thấy leaveBalance"));

        while (!current.isAfter(leave.getEndDate())) {

            if (attendanceRepository.existsByEmployeeAndWorkDate(
                    leave.getEmployee(), current)) {
                current = current.plusDays(1);
                continue;
            }

            Attendance attendance = new Attendance();
            attendance.setEmployee(leave.getEmployee());
            attendance.setWorkDate(current);

            setStatusAndUpdateBalance(leave, leaveBalance, attendance);

            attendance.setCheckInTime(null);
            attendance.setCheckOutTime(null);

            attendanceRepository.save(attendance);

            current = current.plusDays(1);
        }

        leaveBalanceRepository.save(leaveBalance); // cập nhật số dư sau khi xử lý xong
    }

    private void setStatusAndUpdateBalance(LeaveRequest leave,
                                           LeaveBalance leaveBalance,
                                           Attendance attendance) {
        if (leave.getType() == LeaveType.UNPAID) {
            attendance.setStatus(AttendanceStatus.LEAVE_UNPAID);
        } else {
            if (leaveBalance.getRemaining() > 0) {
                attendance.setStatus(AttendanceStatus.LEAVE);
                leaveBalance.setRemaining(leaveBalance.getRemaining() - 1); // trừ phép
            } else {
                attendance.setStatus(AttendanceStatus.LEAVE_UNPAID);
            }
        }
    }

}
