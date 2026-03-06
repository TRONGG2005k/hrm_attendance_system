package com.example.hrm.modules.employee.repository;

import com.example.hrm.modules.employee.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    Page<Contact> findByIsDeletedFalse(Pageable pageable);

    Optional<Contact> findByIdAndIsDeletedFalse(String s);

    List<Contact> findByEmployeeIdAndIsDeletedFalse(String employeeId);

    Page<Contact> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
}
