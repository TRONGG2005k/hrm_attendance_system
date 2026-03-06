package com.example.hrm.modules.attendance.entity;

import com.example.hrm.shared.enums.AttendanceEvaluation;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.AttendanceStatus;

@Entity
@Table(name = "attendance", indexes = {
        @Index(columnList = "employee_id"),
        @Index(columnList = "status")
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
            @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    LocalDateTime checkInTime;
    LocalDateTime checkOutTime;

    long earlyLeaveMinutes;
    long lateMinutes;

    @Enumerated(EnumType.STRING)
    AttendanceStatus status;

    @OneToMany(
            mappedBy = "attendance",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<BreakTime> breaks;

//    @Column(name = "work_date", nullable = false)
    @Column(name = "work_date", nullable = true)
    LocalDate workDate;

    @Enumerated(EnumType.STRING)
    AttendanceEvaluation evaluation;

    // Quan hệ OT
    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<AttendanceOTRate> attendanceOTRates = new ArrayList<>();

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}