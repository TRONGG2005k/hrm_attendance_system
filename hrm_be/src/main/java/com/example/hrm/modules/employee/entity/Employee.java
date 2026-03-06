package com.example.hrm.modules.employee.entity;

import com.example.hrm.modules.organization.entity.Position;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.hrm.modules.contract.entity.Contract;
import com.example.hrm.modules.file.entity.FileAttachment;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import com.example.hrm.shared.enums.ShiftType;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    String code;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Builder.Default
    LocalDate joinDate = LocalDate.now();

    @Column(nullable = false, unique = true)
    String email;

    String phone;

    @ManyToOne
    @JoinColumn(name = "address_id")
    Address address;

    @Enumerated(EnumType.STRING)
    EmployeeStatus status;

    @ManyToOne
    @JoinColumn(name = "position_id")
    Position position;

    @OneToMany(mappedBy = "employee")
    @Builder.Default
    List<Contract> contracts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    ShiftType shiftType;


    @ManyToOne
    @JoinColumn(name = "sub_department_id")
    SubDepartment subDepartment;

    @Transient
    @Builder.Default
    private List<FileAttachment> files = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}
