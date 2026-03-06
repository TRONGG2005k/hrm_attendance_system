package com.example.hrm.modules.attendance.service;


import com.example.hrm.modules.attendance.dto.response.AttendanceRealTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
// import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.enums.ShiftType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.face_recognition.service.FaceRecognitionService;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceScanService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final FaceRecognitionService faceRecognitionService;
    private final BreakTimeService breakTimeService;
    private final AttendanceCheckInService attendanceCheckInService;
    private final AttendanceCheckOutService attendanceCheckOutService;

    public AttendanceRealTimeResponse scan(MultipartFile request) {

        var employeeCode = faceRecognitionService.recognizeFace(request);
        var employee = employeeRepository
                .findByCodeAndIsDeletedFalse(employeeCode.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        var openAttendanceOpt =
                attendanceRepository
                        .findTopByEmployeeAndStatusAndCheckOutTimeIsNullOrderByCheckInTimeDesc(
                                employee, AttendanceStatus.WORKING);

        if (openAttendanceOpt.isEmpty()) {
            return attendanceCheckInService.checkIn(employee);
        }

        Attendance attendance = openAttendanceOpt.get();
        LocalDateTime now = LocalDateTime.now();

        LocalDate workDate =
                employee.getShiftType() == ShiftType.NIGHT
                        && now.toLocalTime().isBefore(LocalTime.of(22, 0))
                        ? now.minusDays(1).toLocalDate()
                        : now.toLocalDate();


        breakTimeService.ensureDefaultBreak(attendance, workDate);

        if (breakTimeService.isInBreak(attendance, now)) {
            throw new AppException(ErrorCode.IN_BREAK_TIME, 400);
        }

        return attendanceCheckOutService.checkOut(attendance);
    }




}
