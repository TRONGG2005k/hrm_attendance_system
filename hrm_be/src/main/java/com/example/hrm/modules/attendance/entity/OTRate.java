package com.example.hrm.modules.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.hrm.shared.enums.OTType;

@Entity
@Table(
        name = "ot_rate",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"date", "type"})}
)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OTRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    LocalDate date; // Ngày áp dụng (có thể null nếu OT áp dụng cho tất cả ngày type này)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OTType type; // NORMAL, WEEKEND, HOLIDAY, NIGHT

    @Column(nullable = false)
    Double rate; // 1.5, 2.0, 3.0

    String description;

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

