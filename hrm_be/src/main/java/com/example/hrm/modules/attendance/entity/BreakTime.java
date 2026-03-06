package com.example.hrm.modules.attendance.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.example.hrm.shared.enums.BreakType;

@Entity
@Table(name = "break_time", indexes = {
        @Index(columnList = "attendance_id"),
        @Index(columnList = "break_start")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BreakTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attendance_id", nullable = false)
    Attendance attendance;

    LocalDateTime breakStart;
    LocalDateTime breakEnd;

    @Enumerated(EnumType.STRING)
    BreakType type;
}

