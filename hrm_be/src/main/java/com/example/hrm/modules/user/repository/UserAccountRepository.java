package com.example.hrm.modules.user.repository;

import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.shared.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    @EntityGraph(attributePaths = {"employee"})
    Page<UserAccount> findByIsDeletedFalseAndStatus(Pageable pageable, UserStatus status);
    Optional<UserAccount> findByUsernameAndIsDeletedFalseAndStatus(String username, UserStatus status);
    Optional<UserAccount> findByIdAndIsDeletedFalseAndStatus(String id, UserStatus status);
    boolean existsByEmployeeIdAndIsDeletedFalse(String id);
    Optional<UserAccount> findByUsernameAndIsDeletedFalse(String username);

}
