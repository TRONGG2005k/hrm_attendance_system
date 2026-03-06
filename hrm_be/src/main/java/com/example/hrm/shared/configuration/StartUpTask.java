package com.example.hrm.shared.configuration;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.user.entity.Role;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.shared.enums.UserStatus;
import com.example.hrm.modules.user.repository.RoleRepository;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StartUpTask {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmployeeRepository employeeRepository;
    @PostConstruct
    public void init(){
        Role role = roleRepository.findByNameAndIsDeletedFalse(com.example.hrm.shared.enums.Role.ROLE_ADMIN.name()).orElseGet(
                ()->roleRepository.save(
                        Role.builder()
                                .name("ROLE_ADMIN")
                                .build()
                )
        );

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        var employee = employeeRepository.findByCodeAndIsDeletedFalse("admin")
                .orElseGet(() -> employeeRepository.save(
                        Employee.builder()
                                .code("admin")
                                .firstName("System")
                                .lastName("Administrator")
                                .email("admin@example.com")
                                .build()
                ));

        userAccountRepository.findByUsernameAndIsDeletedFalseAndStatus("admin", UserStatus.ACTIVE).orElseGet(
                () -> userAccountRepository.save(
                        UserAccount.builder()
                                .username("admin")
                                .employee(employee)
                                .password(passwordEncoder.encode("admin"))
                                .roles(roles)
                                .status(UserStatus.ACTIVE)
                                .build()
                )
        );
    }
}
