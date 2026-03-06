package com.example.hrm.modules.penalty.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.shared.enums.PenaltyType;

@Builder
@Entity
@Table(name = "attendance_penalty")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendancePenalty implements PenaltySource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "attendance_id", nullable = false, unique = true)
    private Attendance attendance;


    private long lateMinutes;
    private long earlyLeaveMinutes;

    @Enumerated(EnumType.STRING)
    private PenaltyType penaltyType;


    private String reason;

    private Integer minutes;

    @Override
    public int getValue() {
        return minutes;
    }

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
