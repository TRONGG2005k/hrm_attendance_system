package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.entity.*;
import com.example.hrm.modules.file.dto.response.FileAttachmentResponse;
import com.example.hrm.modules.file.entity.FileAttachment;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.shared.enums.RefType;
// import com.example.hrm.modules.employee.mapper.AddressMapper;
import com.example.hrm.modules.employee.mapper.ContactMapper;
import com.example.hrm.modules.file.mapper.FileAttachmentMapper;
import com.example.hrm.modules.employee.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.example.hrm.modules.employee.mapper.EmployeeMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import com.example.hrm.modules.employee.dto.request.EmployeeRequest;
import com.example.hrm.modules.employee.dto.response.EmployeeResponse;
import com.example.hrm.modules.file.repository.FileAttachmentRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final AddressRepository addressRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final PositionRepository positionRepository;


    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<EmployeeResponse> getAllEmployees(int page, int size) {
        var employeePage = employeeRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return employeePage.map(employeeMapper::toResponse);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public EmployeeResponse getEmployeeById(String id) {
        var employee = employeeRepository.findEmployeeWithFiles(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id
                                + "or has been deleted"));
        List<FileAttachment> fileAttachments = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(RefType.EMPLOYEE.getValue(), employee.getId());
        List<FileAttachmentResponse> fileAttachmentResponses = fileAttachments.stream().map(
                FileAttachmentMapper.INSTANCE::toResponse).toList();
        var employeeResponse = employeeMapper.toResponse(employee);
        employeeResponse.setContacts(
                contactRepository.findByEmployeeIdAndIsDeletedFalse(employee.getId())
                        .stream().map(contactMapper::toResponse)
                        .toList());
        employeeResponse.setFileAttachmentResponses(fileAttachmentResponses);
        return employeeResponse;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        Employee employee = employeeMapper.toEntity(request);

        employee.setAddress(
                addressRepository.findByIdAndIsDeletedFalse(request.getAddressId())
                        .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404))
        );

        employee.setSubDepartment(
                subDepartmentRepository.findByIdAndIsDeletedFalse(request.getSubDepartmentId())
                        .orElseThrow(() -> new AppException(ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404))
        );

        employee.setPosition(
                positionRepository.findByIdAndActiveTrue(request.getPositionId())
                        .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404))
        );

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }



    @Transactional
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public EmployeeResponse updateEmployee(String id, EmployeeRequest request) {

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));

        employeeMapper.updateEntity(request, employee);

        if (request.getAddressId() != null) {
            employee.setAddress(
                    addressRepository.findByIdAndIsDeletedFalse(request.getAddressId())
                            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404))
            );
        }

        if (request.getSubDepartmentId() != null) {
            employee.setSubDepartment(
                    subDepartmentRepository.findByIdAndIsDeletedFalse(request.getSubDepartmentId())
                            .orElseThrow(() -> new AppException(ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404))
            );
        }

        if (request.getPositionId() != null) {
            employee.setPosition(
                    positionRepository.findByIdAndActiveTrue(request.getPositionId())
                            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404))
            );
        }

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }



    @Transactional
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public void deleteEmployee(String id) {
        var employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id));
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }
}
