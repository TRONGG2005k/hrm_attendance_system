package com.example.hrm.modules.attendance.repository;

import com.example.hrm.modules.attendance.entity.BreakTime;
import com.example.hrm.modules.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BreakTimeRepository extends JpaRepository<BreakTime, String> {

    // Lấy tất cả break của một attendance theo page
    Page<BreakTime> findByAttendance(Attendance attendance, Pageable pageable);

    // Lấy tất cả break của một attendance mà thời điểm hiện tại nằm trong break (theo page)
    Page<BreakTime> findByAttendanceAndBreakStartBeforeAndBreakEndAfter(
            Attendance attendance, LocalDateTime now1, LocalDateTime now2, Pageable pageable);

    // Lấy tất cả break trong khoảng thời gian theo page
    Page<BreakTime> findByAttendanceAndBreakStartBetween(
            Attendance attendance, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
