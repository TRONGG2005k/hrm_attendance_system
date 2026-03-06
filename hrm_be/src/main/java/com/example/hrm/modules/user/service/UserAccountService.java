package com.example.hrm.modules.user.service;

import com.example.hrm.modules.user.dto.request.BatchCreateRequest;
import com.example.hrm.modules.user.dto.request.ChangeRole;
import com.example.hrm.modules.user.dto.request.UserAccountRequest;
import com.example.hrm.modules.user.dto.response.BatchCreateResponse;
import com.example.hrm.modules.user.dto.response.UserAccountResponse;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.shared.enums.Role;
import com.example.hrm.shared.enums.UserStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.user.mapper.RoleMapper;
import com.example.hrm.modules.user.mapper.UserAccountMapper;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.user.repository.RoleRepository;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.modules.auth.service.JwtService;
import com.example.hrm.shared.service.EmailService;
import com.nimbusds.jose.JOSEException;
// import com.example.hrm.modules.user.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountMapper userAccountMapper;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Transactional
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public UserAccountResponse createAuto(String employeeId) throws JOSEException {
        if (userAccountRepository.existsByEmployeeIdAndIsDeletedFalse(employeeId)) {
            throw new AppException(ErrorCode.USER_ACCOUNT_ALREADY_EXISTS, 409);
        }

        var employee = employeeRepository
                .findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        var user = new UserAccount();
        user.setUsername(employee.getCode());          // hoặc email
        user.setEmployee(employee);
        user.setStatus(UserStatus.PENDING_ACTIVE);
        user.setPassword(null);

        var role = roleRepository.findByNameAndIsDeletedFalse(Role.ROLE_EMPLOYEE.name())
                .orElseGet(() -> roleRepository.save(com.example.hrm.modules.user.entity.Role.builder()
                                .name(Role.ROLE_EMPLOYEE.name())
                                .build()
                ));
        user.getRoles().add(role);

        userAccountRepository.save(user);

        // ✅ TẠO ACTIVATION TOKEN
        String activationToken = jwtService.generateActivationToken(user);

        // ✅ GỬI MAIL (QUAN TRỌNG)
        emailService.sendActivationEmail(user, activationToken);

        // build response (KHÔNG token)
        var response = userAccountMapper.toResponse(user);
        var roles = user.getRoles().stream()
                .map(roleMapper::toResponse)
                .toList();
        response.setRoles(roles);

        return response;
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public BatchCreateResponse createAuto(BatchCreateRequest request) throws JOSEException {
        List<UserAccountResponse> userAccountResponseList = new ArrayList<>();
        for(var item : request.getListId()){
            userAccountResponseList.add(createAuto(item));
        }

        return new BatchCreateResponse(userAccountResponseList);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserAccountResponse createManual(UserAccountRequest request){
        var user = userAccountMapper.toEntity(request);
        var employee = employeeRepository
                .findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));
        user.setEmployee(employee);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var role = roleRepository.findByNameAndIsDeletedFalse(Role.ROLE_EMPLOYEE.name())
                .orElseGet(() -> roleRepository.save(com.example.hrm.modules.user.entity.Role.builder()
                                .name(Role.ROLE_EMPLOYEE.name()).build()));
        user.getRoles().add(role);

        userAccountRepository.save(user);
        var response = userAccountMapper.toResponse(user);
        var roles = user.getRoles().stream().map(roleMapper::toResponse).toList();
        response.setRoles(roles);
        return response;
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public Page<UserAccountResponse> getAll(int page, int size){
        var accounts = userAccountRepository.findByIsDeletedFalseAndStatus(PageRequest.of(page, size), UserStatus.ACTIVE);
        return accounts.map(item -> {
            var roles = item.getRoles().stream().map(roleMapper::toResponse).toList();
            var response = userAccountMapper.toResponse(item);
            response.setRoles(roles);
            return response;
        });
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_MANAGER', 'ADMIN')")
    public UserAccountResponse getById(String id) {
        var user = userAccountRepository.findByIdAndIsDeletedFalseAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        var response = userAccountMapper.toResponse(user);
        var roles = user.getRoles().stream().map(roleMapper::toResponse).toList();
        response.setRoles(roles);

        return response;
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_MANAGER', 'ADMIN')")
    public UserAccountResponse update(String id, UserAccountRequest request) {
        var user = userAccountRepository.findByIdAndIsDeletedFalseAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));
        // Update employee
        if (request.getEmployeeId() != null) {
            var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));

            user.setEmployee(employee);
        }
        // Update password nếu FE truyền
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        // Các field khác như username, email (nếu có):
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if(request.getStatus() != null) user.setStatus(request.getStatus());
        userAccountRepository.save(user);
        // Build response
        var response = userAccountMapper.toResponse(user);
        response.setStatus(user.getStatus().name());
        var roles = user.getRoles().stream().map(roleMapper::toResponse).toList();
        response.setRoles(roles);

        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public UserAccountResponse changeRole(ChangeRole request, String id){

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserAccount currentUser = userAccountRepository.findByUsernameAndIsDeletedFalse(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404)) ;

        var user = userAccountRepository
                .findByIdAndIsDeletedFalseAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND, 404));

        log.info("AUTH = {}", authentication.getAuthorities());
        // ❌ không đổi role chính mình
        if(currentUser.getId().equals(user.getId())){
            throw new AppException(ErrorCode.PERMISSION_DENIED,403,
                    "Không thể thay đổi role của chính mình");
        }

        boolean currentIsAdmin =
                currentUser.getRoles()
                        .stream()
                        .anyMatch(r ->
                                r.getName().equals(Role.ROLE_ADMIN.name()));

        for (var item : request.getChangeRole().entrySet()) {

            var role = roleRepository
                    .findByIdAndIsDeletedFalse(item.getKey())
                    .orElseThrow(() ->
                            new AppException(ErrorCode.ROLE_NOT_FOUND,404));

            // ❌ chỉ ADMIN mới gán ADMIN
            if(role.getName().equals(Role.ROLE_ADMIN.name())
                    && !currentIsAdmin){
                throw new AppException(ErrorCode.PERMISSION_DENIED,
                        403,
                        "Bạn không có quyền gán ADMIN");
            }

            if(item.getValue()){
                user.getRoles().add(role);
            }else{
                user.getRoles().remove(role);
            }
        }

        return userAccountMapper
                .toResponse(userAccountRepository.save(user));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        var user = userAccountRepository.findByIdAndIsDeletedFalseAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        user.setDeletedAt(LocalDateTime.now());
        user.setIsDeleted(true);

        userAccountRepository.save(user);
    }


}
