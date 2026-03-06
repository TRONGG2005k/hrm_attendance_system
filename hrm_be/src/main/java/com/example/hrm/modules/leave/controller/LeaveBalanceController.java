package com.example.hrm.modules.leave.controller;

import com.example.hrm.modules.leave.dto.response.LeaveBalanceResponse;
import com.example.hrm.modules.leave.entity.LeaveBalance;
import com.example.hrm.modules.leave.service.LeaveBalanceService;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/leave-balance")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;
    private final UserAccountRepository userAccountRepository;

    @GetMapping("/me")
    public LeaveBalanceResponse getMyLeaveBalance(
            @RequestParam(required = false) Integer year
    ) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        UserAccount user = userAccountRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        LeaveBalance balance = leaveBalanceService
                .getLeaveBalance(user.getEmployee(), targetYear);

        return new LeaveBalanceResponse(
                balance.getYear(),
                balance.getTotalEntitled(),
                balance.getUsed(),
                balance.getRemaining()
        );
    }
}
