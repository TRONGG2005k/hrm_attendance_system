package com.example.hrm.modules.organization.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "positions")
@Setter
@Getter
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String code; // VD: DEV_JUNIOR, SALE_LEADER

    @Column(nullable = false)
    private String name; // Tên chức vụ

    private String description;

    private Boolean active = true;

    private Boolean isDeleted;

    // getter / setter
}

