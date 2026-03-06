package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.request.BreakTimeBatchRequest;
import com.example.hrm.modules.attendance.dto.request.BreakTimeRequest;
import com.example.hrm.modules.attendance.dto.response.BreakTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.BreakTime;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.ShiftType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.attendance.repository.BreakTimeRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BreakTimeService {

    private final BreakTimeRepository breakTimeRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public void ensureDefaultBreak(Attendance attendance, LocalDate workDate) {

        if (!attendance.getBreaks().isEmpty()) return;

        ShiftType shiftType = attendance.getEmployee().getShiftType();

        LocalTime breakStart = shiftType == ShiftType.NIGHT
                ? LocalTime.of(2, 0)
                : LocalTime.of(12, 0);

        BreakTime breakTime = new BreakTime();
        breakTime.setAttendance(attendance);
        breakTime.setBreakStart(LocalDateTime.of(workDate, breakStart));
        breakTime.setBreakEnd(LocalDateTime.of(workDate, breakStart.plusHours(1)));

        attendance.getBreaks().add(breakTime);
        breakTimeRepository.save(breakTime);
    }

    public boolean isInBreak(Attendance attendance, LocalDateTime now) {
        return attendance.getBreaks()
                .stream()
                .anyMatch(b ->
                        !now.isBefore(b.getBreakStart())
                                && !now.isAfter(b.getBreakEnd())
                );
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public List<String> updateBreakForSubDepartment(BreakTimeBatchRequest request) {
        // Lấy tất cả nhân viên trong phòng ban
        List<Employee> employees = employeeRepository.findBySubDepartmentId(request.getSubDepartmentId());

        // Lấy attendance mới nhất của từng nhân viên
        List<Attendance> attendances = employees.stream()
                .map(e -> attendanceRepository.findTopByEmployeeOrderByCheckInTimeDesc(e).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<String> updatedAttendanceIds = new ArrayList<>();

        List<BreakTime> breaksToSave = new ArrayList<>();
        for (Attendance attendance : attendances) {
            if (attendance.getBreaks() == null) {
                attendance.setBreaks(new ArrayList<>());
            }

            BreakTime breakTime = new BreakTime();
            breakTime.setAttendance(attendance);
            breakTime.setBreakStart(request.getBreakStart());
            breakTime.setBreakEnd(request.getBreakEnd());
            breakTime.setType(request.getType());

            attendance.getBreaks().add(breakTime);
            breaksToSave.add(breakTime);

            updatedAttendanceIds.add(attendance.getId());
        }

        // Batch save để tối ưu
        breakTimeRepository.saveAll(breaksToSave);

        return updatedAttendanceIds; // trả về danh sách attendance đã cập nhật
    }

    // Tạo break mới
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public BreakTimeResponse createBreak(BreakTimeRequest request) {
        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));

        BreakTime breakTime = new BreakTime();
        breakTime.setAttendance(attendance);
        breakTime.setBreakStart(request.getBreakStart());
        breakTime.setBreakEnd(request.getBreakEnd());
        breakTime.setType(request.getType());

        breakTimeRepository.save(breakTime);
        return mapToResponse(breakTime);
    }

    // Cập nhật break
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public BreakTimeResponse updateBreak(String id, BreakTimeRequest request) {
        BreakTime breakTime = breakTimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));

        breakTime.setBreakStart(request.getBreakStart());
        breakTime.setBreakEnd(request.getBreakEnd());
        breakTime.setType(request.getType());

        breakTimeRepository.save(breakTime);
        return mapToResponse(breakTime);
    }

    // Xóa break
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public void deleteBreak(String id) {
        BreakTime breakTime = breakTimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));
        breakTimeRepository.delete(breakTime);
    }

    // Lấy break theo attendance (paging)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<BreakTimeResponse> getBreaksByAttendance(String attendanceId, Pageable pageable) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));

        return breakTimeRepository.findByAttendance(attendance, pageable)
                .map(this::mapToResponse);
    }

    private BreakTimeResponse mapToResponse(BreakTime breakTime) {
        return BreakTimeResponse.builder()
                .id(breakTime.getId())
                .attendanceId(breakTime.getAttendance().getId())
                .breakStart(breakTime.getBreakStart())
                .breakEnd(breakTime.getBreakEnd())
                .type(breakTime.getType())
                .build();
    }
}
