package com.example.hrm.modules.contract.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.file.entity.FileAttachment;
import com.example.hrm.shared.enums.ContractStatus;
import com.example.hrm.shared.enums.ContractType;

@Entity
@Table(name = "contracts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "code"})
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType type;

    private LocalDate signDate;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private Double baseSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    private String note;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Transient
    @Builder.Default
    private List<FileAttachment> files = new ArrayList<>();

    @OneToMany(
            mappedBy = "contract",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<SalaryContract> salaryContracts = new ArrayList<>();
    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

