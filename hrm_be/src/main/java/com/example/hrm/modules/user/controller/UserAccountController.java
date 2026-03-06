package com.example.hrm.modules.user.controller;

import com.example.hrm.modules.user.dto.request.BatchCreateRequest;
import com.example.hrm.modules.user.dto.request.ChangeRole;
import com.example.hrm.modules.user.dto.request.UserAccountRequest;
import com.example.hrm.modules.user.dto.response.BatchCreateResponse;
import com.example.hrm.modules.user.dto.response.UserAccountResponse;
import com.example.hrm.modules.user.service.UserAccountService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/user-accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<UserAccountResponse> createUserAccount(
            @Valid @RequestBody UserAccountRequest request
    ) {
        var response = userAccountService.createManual(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auto/{id}")
    public ResponseEntity<UserAccountResponse> createUserAccount(@PathVariable  String id) throws JOSEException {
        var response = userAccountService.createAuto(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auto")
    public ResponseEntity<BatchCreateResponse> createUserAccount(@RequestBody BatchCreateRequest request) throws JOSEException {
        var response = userAccountService.createAuto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserAccountResponse>> getAllUserAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var response = userAccountService.getAll(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponse> getUserAccountById(
            @PathVariable String id
    ) {
        var response = userAccountService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountResponse> updateUserAccount(
            @PathVariable String id,
            @Valid @RequestBody UserAccountRequest request
    ) {
        var response = userAccountService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAccount(
            @PathVariable String id
    ) {
        userAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/change-role")
    public ResponseEntity<UserAccountResponse> changeRole(
            @PathVariable String id,
            @Valid @RequestBody ChangeRole request
    ) {
        var response = userAccountService.changeRole(request, id);
        return ResponseEntity.ok(response);
    }
}
