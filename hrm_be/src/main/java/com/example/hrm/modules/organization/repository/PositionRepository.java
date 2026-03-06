package com.example.hrm.modules.organization.repository;

import com.example.hrm.modules.organization.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {

    boolean existsByCode(String code);
    List<Position> findAllByIsDeletedFalseAndActiveTrue();
    Optional<Position> findByIdAndActiveTrue(String id);
    Optional<Position> findByNameAndIsDeletedFalse(String name);
    boolean existsByNameAndIsDeletedFalse(String name);
    Optional<Position> findByCodeAndIsDeletedFalse(String code);
//    Optional<Position> findByIsDeletedFalse(String id);
}
