package com.example.hrm.modules.leave.entity;

import com.example.hrm.modules.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leave_balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    int year;

    int totalEntitled;   // Tổng phép được hưởng
    int used;            // Đã dùng
    int remaining;       // Còn lại

    @Builder.Default
    Boolean isDeleted = false;
}
