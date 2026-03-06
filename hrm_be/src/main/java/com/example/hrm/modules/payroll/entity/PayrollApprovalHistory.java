package com.example.hrm.modules.payroll.entity;


import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.PayrollApprovalStatus;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_approval_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    Integer month;

    @Column(nullable = false)
    Integer year;

    @Column(nullable = false)
    Double totalAmount; // tổng tiền lương cả tháng

    // JSON snapshot danh sách payroll tại thời điểm duyệt
    @Column(columnDefinition = "LONGTEXT")
    String payrollSnapshot;

    @ManyToOne
    @JoinColumn(name = "approved_by_id", nullable = false)
    Employee approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PayrollApprovalStatus status;  // APPROVED, REJECTED

    String comment;

    @Builder.Default
    LocalDateTime approvedAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}

