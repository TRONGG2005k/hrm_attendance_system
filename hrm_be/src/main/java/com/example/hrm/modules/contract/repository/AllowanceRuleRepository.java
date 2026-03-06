package com.example.hrm.modules.contract.repository;

import com.example.hrm.modules.contract.entity.AllowanceRule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AllowanceRuleRepository
        extends JpaRepository<AllowanceRule, Long> {

    @Query("""
                SELECT r FROM AllowanceRule r
                WHERE r.active = true
                AND (:positionId IS NULL OR r.position.id = :positionId OR r.position IS NULL)
                AND (:deptId IS NULL OR r.subDepartment.id = :deptId OR r.subDepartment IS NULL)
            """)
    List<AllowanceRule> findActiveRules(
            @Param("positionId") String positionId,
            @Param("deptId") String deptId);

    @EntityGraph(attributePaths = { "position", "subDepartment", "allowance" })
    List<AllowanceRule> findAllByActiveTrue();

    boolean existsByAllowanceCodeAndPositionIdAndSubDepartmentIdAndActiveTrue(String code, String position,
            String subDepartment);
}
