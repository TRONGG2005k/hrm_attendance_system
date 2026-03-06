package com.example.hrm.modules.contract.entity;


import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.AdjustmentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_adjustment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AdjustmentType type; // BONUS / PENALTY

    @Column(nullable = false, precision = 15, scale = 0)
    BigDecimal amount;

    String description;

    @Column(nullable = false)
    LocalDate appliedDate;

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
}
