package com.example.hrm.modules.employee.repository;

import com.example.hrm.modules.employee.entity.Employee;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @EntityGraph(attributePaths = { "address", "subDepartment" })
    Page<Employee> findByIsDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = { "address", "subDepartment", "position", "contracts" })
    List<Employee> findAllByIsDeletedFalse();

    Optional<Employee> findByIdAndIsDeletedFalse(String id);

    boolean existsByCodeAndIsDeletedFalse(String codee);

    Optional<Employee> findByCodeAndIsDeletedFalse(String id);

    @Query("""
                SELECT e FROM Employee e
                LEFT JOIN FETCH e.subDepartment sd
                LEFT JOIN FileAttachment f ON f.refId = e.id AND f.refType = 'EMPLOYEE'
                WHERE e.id = :employeeId
            """)
    Optional<Employee> findEmployeeWithFiles(@Param("employeeId") String employeeId);

    @Query("SELECT e FROM Employee e WHERE e.subDepartment.id = :subDeptId AND e.isDeleted = false")
    List<Employee> findBySubDepartmentId(@Param("subDeptId") String subDeptId);

}
