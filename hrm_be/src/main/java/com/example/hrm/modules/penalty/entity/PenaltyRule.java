package com.example.hrm.modules.penalty.entity;
import com.example.hrm.shared.enums.PenaltyType;
import com.example.hrm.shared.enums.BasedOn;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "penalty_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "min_value", nullable = false)
    private Integer minValue;

    @Column(name = "max_value")
    private Integer maxValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_type", nullable = false)
    private PenaltyType penaltyType;

    @Column(name = "penalty_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "based_on", nullable = false)
    private BasedOn basedOn;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}


