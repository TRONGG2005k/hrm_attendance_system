package com.example.hrm.modules.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Entity
@Table(name = "attendance_ot_rate")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceOTRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "attendance_id", nullable = false)
    Attendance attendance;

    // Lưu OT rate như 1 entity riêng để linh hoạt
    @ManyToOne
    @JoinColumn(name = "ot_rate_id", nullable = false)
    OTRate otRate;

    @Column(nullable = false)
    @Builder.Default
    Double otHours = 0.0; // số giờ tăng ca

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}

