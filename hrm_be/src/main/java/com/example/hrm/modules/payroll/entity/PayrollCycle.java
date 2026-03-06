package com.example.hrm.modules.payroll.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Table(
    name = "payroll_cycle",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_payroll_cycle_day",
            columnNames = {"start_day", "end_day"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ngày bắt đầu tính công (1–31)
     */
    @Column(name = "start_day", nullable = false)
    private Integer startDay;

    /**
     * Ngày kết thúc tính công (1–31)
     */
    @Column(name = "end_day", nullable = false)
    private Integer endDay;

    @Column(nullable = false)
    private Integer workingDays;

    /**
     * Ngày trả lương (1–31)
     */
    @Column(nullable = false)
    private Integer payday;

    /**
     * Chu kỳ đang được sử dụng
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;
}

