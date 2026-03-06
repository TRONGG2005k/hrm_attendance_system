package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.ContactRequest;
import com.example.hrm.modules.employee.dto.response.ContactResponse;
import com.example.hrm.modules.employee.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /** CREATE */
    @PostMapping
    public ContactResponse create(@RequestBody ContactRequest request) {
        return contactService.create(request);
    }

    /** GET ALL WITH PAGINATION */
    @GetMapping
    public Page<ContactResponse> getAllContact(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return contactService.getAllContact(page, size);
    }

    /** GET ONE */
    @GetMapping("/{id}")
    public ContactResponse getById(@PathVariable String id) {
        return contactService.getById(id);
    }

    /** UPDATE */
    @PutMapping("/{id}")
    public ContactResponse update(
            @PathVariable String id,
            @RequestBody ContactRequest request
    ) {
        return contactService.update(id, request);
    }

    /** SOFT DELETE */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        contactService.delete(id);
    }
}
