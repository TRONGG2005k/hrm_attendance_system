package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.dto.request.ContactRequest;
import com.example.hrm.modules.employee.dto.response.ContactResponse;
import com.example.hrm.modules.employee.entity.Contact;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.employee.mapper.ContactMapper;
import com.example.hrm.modules.employee.repository.ContactRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final EmployeeRepository employeeRepository;

    /** CREATE */
    @Transactional
    public ContactResponse create(ContactRequest request) {
        var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));

        var contact = contactMapper.toEntity(request);
        contact.setEmployee(employee);

        var saved = contactRepository.save(contact);
        return contactMapper.toResponse(saved);
    }

    /** GET ALL (Paged) */
    public Page<ContactResponse> getAllContact(int page, int size) {
        Page<Contact> contacts = contactRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return contacts.map(contactMapper::toResponse);
    }

    /** GET ONE */
    public ContactResponse getById(String id) {
        var contact = contactRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTACT_NOT_FOUND, 404));

        return contactMapper.toResponse(contact);
    }

    /** UPDATE */
    @Transactional
    public ContactResponse update(String id, ContactRequest request) {

        var contact = contactRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTACT_NOT_FOUND, 404));

        // Kiểm tra employeeId mới có tồn tại không
        var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));

        // UPDATE FIELDS
        contact.setType(request.getType());
        contact.setValue(request.getValue());
        contact.setRelation(request.getRelation());
        contact.setNote(request.getNote());
        contact.setEmployee(employee);
        contact.setUpdatedAt(LocalDateTime.now());

        return contactMapper.toResponse(contact);
    }

    /** SOFT DELETE */
    @Transactional
    public void delete(String id) {
        var contact = contactRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTACT_NOT_FOUND, 404));

        contact.setDeletedAt(LocalDateTime.now());
        contact.setIsDeleted(true);
    }
}
