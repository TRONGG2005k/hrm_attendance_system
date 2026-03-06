package com.example.hrm.modules.organization.service;

import com.example.hrm.modules.organization.dto.request.DepartmentRequest;
import com.example.hrm.modules.organization.dto.response.DepartmentResponse;
import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.employee.mapper.SubDepartmentMapper;
import com.example.hrm.modules.organization.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final SubDepartmentMapper subDepartmentMapper;
    private final DepartmentRepository departmentRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        departmentRepository.save(department);
        return toResponse(department);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findByIsDeletedFalse(pageable)
                .map(this::toResponse);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public DepartmentResponse getDepartmentById(String id) {
        var department = departmentRepository.findByIdWithSubDepartments(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND, 404));

        var departmentResponse = toResponse(department);

        departmentResponse.setSubDepartmentResponses(
                department.getSubDepartments().stream()
                        .map(sub -> {
                            var dto = subDepartmentMapper.toResponse(sub);
                            dto.setDepartmentId(department.getId());
                            return dto;
                        })
                        .collect(Collectors.toSet())
        );
        return departmentResponse;
    }



    @Transactional
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public DepartmentResponse updateDepartment(String id, DepartmentRequest request) {

        if (departmentRepository.existsByNameAndIsDeletedFalseAndIdNot(request.getName(), id)) {
            throw new AppException(ErrorCode.DEPARTMENT_NAME_ALREADY_EXISTS, 500);
        }

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setName(request.getName());
        department.setDescription(request.getDescription());

        return toResponse(department);
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        department.setIsDeleted(true);
        department.setDeletedAt(java.time.LocalDateTime.now());
        departmentRepository.save(department);
    }


    private DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .subDepartmentResponses(department.getSubDepartments().stream().map(
                        subDepartmentMapper::toResponse
                ).collect(Collectors.toSet()))
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .isDeleted(department.getIsDeleted())
                .deletedAt(department.getDeletedAt())
                .build();
    }
}
