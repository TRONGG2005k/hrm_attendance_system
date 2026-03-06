package com.example.hrm.modules.contract.entity;

import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.shared.enums.AllowanceCalculationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "allowance_rule")
@Getter @Setter
public class AllowanceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "allowance_id", nullable = false)
    Allowance allowance;

    /** Có thể null → áp dụng cho mọi chức vụ */
    @ManyToOne
    @JoinColumn(name = "position_id")
    Position position;

    /** Có thể null → áp dụng cho mọi phòng ban */
    @ManyToOne
    @JoinColumn(name = "department_id")
    SubDepartment subDepartment;

    @Column(nullable = false)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    AllowanceCalculationType calculationType;

    @Column(nullable = false)
    Boolean active = true;
}
