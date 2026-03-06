package com.example.hrm.modules.payroll.entity;



import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.PayrollStatus;

@Entity
@Table(
        name = "payroll",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"employee_id", "month"})
        },
        indexes = {
                @Index(columnList = "employee_id"),
                @Index(columnList = "month"),
                @Index(columnList = "status")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Column(nullable = false, length = 7)
    YearMonth month;  // yyyy-MM

    BigDecimal baseSalary;
    BigDecimal allowance;
    Double overtime;
    BigDecimal bonus;
    BigDecimal penalty;
    BigDecimal unpaidLeave;

    BigDecimal totalSalary;

    String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PayrollStatus status;  // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, PAID

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


