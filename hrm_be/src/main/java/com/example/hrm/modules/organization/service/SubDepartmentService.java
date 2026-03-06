package com.example.hrm.modules.organization.service;

import com.example.hrm.modules.organization.dto.request.SubDepartmentRequest;
import com.example.hrm.modules.organization.dto.response.SubDepartmentResponse;
import com.example.hrm.modules.organization.dto.response.SubDepartmentResponseDetail;
import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.employee.mapper.EmployeeMapper;
import com.example.hrm.modules.organization.repository.DepartmentRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SubDepartmentService {

    private final SubDepartmentRepository subDepartmentRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public SubDepartmentResponse createSubDepartment(SubDepartmentRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        SubDepartment subDepartment = SubDepartment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .department(department)
                .build();

        subDepartmentRepository.save(subDepartment);
        return toResponse(subDepartment);
    }

    @PreAuthorize("isAuthenticated()")
    public Page<SubDepartmentResponse> getAllSubDepartments(Pageable pageable) {
        return subDepartmentRepository.findByIsDeletedFalse(pageable)
                .map(this::toResponse);
    }

    @PreAuthorize("isAuthenticated()")
    public Page<SubDepartmentResponse> getSubDepartmentsByDepartment(String departmentId, Pageable pageable) {
        return subDepartmentRepository.findByDepartmentIdAndIsDeletedFalse(departmentId, pageable)
                .map(this::toResponse);
    }

    @PreAuthorize("isAuthenticated()")
    public SubDepartmentResponseDetail getSubDepartmentById(String id) {

        var subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_DEPARTMENT_DEPARTMENT_NOT_FOUND, 404));
        var employees = employeeRepository.findBySubDepartmentId(id);

        return SubDepartmentResponseDetail.builder()
                .id(subDepartment.getId())
                .employeeResponses(employees.stream().map(employeeMapper::toResponse).toList())
                .name(subDepartment.getName())
                .departmentId(subDepartment.getDepartment().getId())
                .description(subDepartment.getDescription())
                .build();
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public SubDepartmentResponse updateSubDepartment(String id, SubDepartmentRequest request) {
        SubDepartment subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubDepartment not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        subDepartment.setName(request.getName());
        subDepartment.setDescription(request.getDescription());
        subDepartment.setDepartment(department);

        subDepartmentRepository.save(subDepartment);
        return toResponse(subDepartment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSubDepartment(String id) {
        SubDepartment subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubDepartment not found"));
        subDepartment.setIsDeleted(true);
        subDepartment.setDeletedAt(java.time.LocalDateTime.now());
        subDepartmentRepository.save(subDepartment);
    }




    private SubDepartmentResponse toResponse(SubDepartment subDepartment) {
        return SubDepartmentResponse.builder()
                .id(subDepartment.getId())
                .departmentId(subDepartment.getDepartment().getId())
                .departmentName(subDepartment.getDepartment().getName())
                .name(subDepartment.getName())
                .description(subDepartment.getDescription())
                .createdAt(subDepartment.getCreatedAt())
                .updatedAt(subDepartment.getUpdatedAt())
                .isDeleted(subDepartment.getIsDeleted())
                .deletedAt(subDepartment.getDeletedAt())
                .build();
    }
}
