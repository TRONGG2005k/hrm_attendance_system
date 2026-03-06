package com.example.hrm.modules.leave.entity;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.LeaveStatus;
import com.example.hrm.shared.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@Table(name = "leave_request")
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveType type; // ANNUAL, UNPAID, SICK, etc.

    @Enumerated(EnumType.STRING)
    private LeaveStatus status; // PENDING, APPROVED, REJECTED, CANCELLED

    private String reason;

    private LocalDateTime approvedAt;

    @ManyToOne
    private Employee approvedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Boolean isDeleted = false;

    private BigDecimal totalDays;

}
