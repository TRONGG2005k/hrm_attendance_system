package com.example.hrm.modules.payroll.repository;

import com.example.hrm.modules.payroll.entity.PayrollApprovalHistory;
import com.example.hrm.shared.enums.PayrollApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollApprovalHistoryRepository extends JpaRepository<PayrollApprovalHistory, String> {
    Page<PayrollApprovalHistory> findByIsDeletedFalse(Pageable pageable);
    Page<PayrollApprovalHistory> findByMonthAndYearAndIsDeletedFalse(Integer month, Integer year, Pageable pageable);
    Page<PayrollApprovalHistory> findByApprovedByIdAndIsDeletedFalse(String approvedById, Pageable pageable);
    Page<PayrollApprovalHistory> findByStatusAndIsDeletedFalse(PayrollApprovalStatus status, Pageable pageable);
}
