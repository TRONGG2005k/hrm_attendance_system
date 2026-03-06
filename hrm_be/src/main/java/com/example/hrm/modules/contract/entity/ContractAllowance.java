package com.example.hrm.modules.contract.entity;

import com.example.hrm.shared.enums.AllowanceCalculationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contract_allowance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractAllowance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private SalaryContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowance_id", nullable = false)
    private Allowance allowance;

    /**
     * Số tiền phụ cấp áp dụng cho hợp đồng này
     * (snapshot – KHÔNG lấy từ rule)
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Có thể dùng sau:
     * - PER_DAY
     * - FIXED
     */
    @Enumerated(EnumType.STRING)
    private AllowanceCalculationType calculationType;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}
