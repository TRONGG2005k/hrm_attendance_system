package com.example.hrm.modules.employee.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String street;     // số nhà + tên đường

    // Thay vì text, lưu khóa ngoại đến bảng Ward
    @ManyToOne
    @JoinColumn(name = "ward_id", nullable = false)
    Ward ward;


    // country vẫn giữ text, mặc định "Vietnam"
    @Builder.Default
    String country = "Vietnam";

    String note;       // ghi chú thêm

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

