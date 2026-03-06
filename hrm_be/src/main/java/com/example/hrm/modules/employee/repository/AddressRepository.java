package com.example.hrm.modules.employee.repository;

import com.example.hrm.modules.employee.entity.Address;
import com.example.hrm.modules.employee.entity.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    Page<Address> findByIsDeletedFalse(Pageable pageable);
    Optional<Address> findByIdAndIsDeletedFalse(String id);
    Optional<Address> findByStreetAndWardAndIsDeletedFalse(String street, Ward ward);
}
