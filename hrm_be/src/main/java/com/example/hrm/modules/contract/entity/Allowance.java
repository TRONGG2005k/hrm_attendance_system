package com.example.hrm.modules.contract.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "allowance")
@Getter
@Setter
public class Allowance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true)
    String code; // VD: MEAL, FUEL, RESPONSIBILITY

    @Column(nullable = false)
    String name;

    String description;

    @Column(nullable = false)
    Boolean active = true;
}

