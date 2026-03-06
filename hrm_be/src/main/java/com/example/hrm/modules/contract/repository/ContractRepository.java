package com.example.hrm.modules.contract.repository;

import com.example.hrm.modules.contract.entity.Contract;
import com.example.hrm.modules.contract.excel.dto.ContractExcelDto;
import com.example.hrm.shared.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    @EntityGraph(attributePaths = { "employee" })
    Page<Contract> findByIsDeletedFalseAndStatus(Pageable pageable, ContractStatus status);

    Page<Contract> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);

    Optional<Contract> findByIdAndIsDeletedFalse(String id);

    boolean existsByEmployee_CodeAndCodeAndIsDeletedFalse(String employeecode, String contractCode);

    Page<Contract> findByIsDeletedFalseAndStatusNot(Pageable pageable, ContractStatus status);

    Optional<Contract> findByEmployeeIdAndStatusAndIsDeletedFalse(
            String employeeId,
            ContractStatus status);

    @Query("""
            SELECT new com.example.hrm.modules.contract.excel.dto.ContractExcelDto(
                e.code,
                c.code,
                c.type,
                c.signDate,
                c.startDate,
                c.endDate,
                c.status,
                sc.baseSalary,
                sc.salaryCoefficient,
                sc.effectiveDate,
                sc.status,
                c.note
            )
            FROM Contract c
            JOIN c.employee e
            JOIN SalaryContract sc ON sc.contract.id = c.id
            WHERE c.isDeleted = false
            """)
    List<ContractExcelDto> findAllForExcel();

}
