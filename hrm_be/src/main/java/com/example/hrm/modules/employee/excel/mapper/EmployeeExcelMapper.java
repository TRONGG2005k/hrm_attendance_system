package com.example.hrm.modules.employee.excel.mapper;

import com.example.hrm.modules.employee.entity.Address;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelExportDto;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.modules.employee.service.AddressResolverService;
import com.example.hrm.shared.mapper.EnumMapper;
import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeExcelMapper {

    private final EnumMapper enumMapper;
    private final AddressResolverService addressResolverService;
    private final SubDepartmentRepository subDepartmentRepository;
    private final PositionRepository positionRepository;

    public Employee toEntity(EmployeeExcelImportDto dto) {
        Employee employee = new Employee();

        // ===== Required fields =====
        employee.setCode(dto.getCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());

        // ===== Optional fields =====
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setPhone(dto.getPhone());
        employee.setGender(enumMapper.mapGender(dto.getGender()));
        employee.setStatus(
                dto.getStatus() != null
                        ? enumMapper.mapEmployeeStatus(dto.getStatus())
                        : EmployeeStatus.ACTIVE
        );
        employee.setJoinDate(dto.getJoinDate() != null ? dto.getJoinDate() : employee.getJoinDate());
        employee.setShiftType(dto.getShiftType());
        // ===== Address =====
        Address address = addressResolverService.resolveAddress(
                dto.getProvince(),
                dto.getDistrict(),
                dto.getWard(),
                dto.getStreet()
        );
        employee.setAddress(address);

        // ===== Organization =====
        employee.setSubDepartment(
                subDepartmentRepository.findByNameAndIsDeletedFalse(dto.getDepartmentName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban: " + dto.getDepartmentName()))
        );

        employee.setPosition(
                positionRepository.findByNameAndIsDeletedFalse(dto.getPositionName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ: " + dto.getPositionName()))
        );

        // ===== Defaults =====
//        employee.setShiftType(null); // nếu Excel chưa có ca làm
        employee.setIsDeleted(false);

        return employee;
    }

    public void updateEntity(Employee employee, EmployeeExcelImportDto dto) {

        // ===== Required fields =====
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());

        // ===== Optional fields =====
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setPhone(dto.getPhone());
        employee.setGender(enumMapper.mapGender(dto.getGender()));
        employee.setStatus(
                dto.getStatus() != null
                        ? enumMapper.mapEmployeeStatus(dto.getStatus())
                        : employee.getStatus()
        );
        employee.setJoinDate(dto.getJoinDate());
        employee.setShiftType(dto.getShiftType());

        // ===== Address =====
        Address address = addressResolverService.resolveAddress(
                dto.getProvince(),
                dto.getDistrict(),
                dto.getWard(),
                dto.getStreet()
        );
        employee.setAddress(address);

        // ===== Organization =====
        employee.setSubDepartment(
                subDepartmentRepository.findByNameAndIsDeletedFalse(dto.getDepartmentName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban: " + dto.getDepartmentName()))
        );

        employee.setPosition(
                positionRepository.findByNameAndIsDeletedFalse(dto.getPositionName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ: " + dto.getPositionName()))
        );
    }

    public EmployeeExcelExportDto toDto(Employee employee){
        SubDepartment subDepartment = subDepartmentRepository.findByIdAndIsDeletedFalse(
                employee.getSubDepartment().getId())
                .orElseThrow(() -> new AppException(ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404));
        return EmployeeExcelExportDto.builder()
                .code(employee.getCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender().toString())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .status(employee.getStatus().toString())
                .joinDate(employee.getJoinDate())
                .shiftType(employee.getShiftType())
                .street(employee.getAddress().getStreet())
                .ward(employee.getAddress().getWard().getName())
                .district(employee.getAddress().getWard().getDistrict().getName())
                .province(employee.getAddress().getWard().getDistrict().getProvince().getName())
                .departmentName(subDepartment.getName())
                .positionName(employee.getPosition().getName())
                .build();
    }
}
