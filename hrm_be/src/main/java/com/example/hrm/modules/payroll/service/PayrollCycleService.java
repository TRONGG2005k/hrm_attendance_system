package com.example.hrm.modules.payroll.service;

import com.example.hrm.modules.payroll.dto.request.PayrollCycleRequest;
import com.example.hrm.modules.payroll.dto.response.PayrollCycleResponse;
import com.example.hrm.modules.payroll.entity.PayrollCycle;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.payroll.repository.PayrollCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PayrollCycleService {

    private final PayrollCycleRepository payrollCycleRepository;

    public PayrollCycleResponse create(
        PayrollCycleRequest request,
        String creatorId
    ) {
        validate(request);

        if (payrollCycleRepository.existsByStartDayAndEndDay(
                request.getStartDay(),
                request.getEndDay())) {
            throw new AppException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                500,
                "Chu kỳ lương này đã tồn tại"
            );
        }

        // Disable cycle cũ nếu muốn chỉ 1 cycle active
        payrollCycleRepository.findByActiveTrue()
            .ifPresent(c -> {
                c.setActive(false);
                payrollCycleRepository.save(c);
            });

        PayrollCycle cycle = PayrollCycle.builder()
            .startDay(request.getStartDay())
            .endDay(request.getEndDay())
            .payday(request.getPayday())
            .active(true)
            .createdAt(LocalDateTime.now())
            .createdBy(creatorId)
            .build();

        payrollCycleRepository.save(cycle);

        return toResponse(cycle);
    }

    public PayrollCycleResponse getActive() {
        return payrollCycleRepository.findByActiveTrue()
            .map(this::toResponse)
            .orElseThrow(() -> new AppException(
                ErrorCode.NOT_FOUND,
                404,
                "Chưa cấu hình chu kỳ lương"
            ));
    }

    public List<PayrollCycleResponse> getHistory() {
        return payrollCycleRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private void validate(PayrollCycleRequest request) {
        int start = request.getStartDay();
        int end = request.getEndDay();
        int payday = request.getPayday();

        if (start < 1 || start > 31 ||
            end < 1 || end > 31 ||
            payday < 1 || payday > 31) {
            throw new AppException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                500,
                "Ngày phải nằm trong khoảng 1–31"
            );
        }

        if (start == end) {
            throw new AppException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                500,
                "Ngày bắt đầu và kết thúc không thể trùng nhau"
            );
        }
    }

    private PayrollCycleResponse toResponse(PayrollCycle cycle) {
        return PayrollCycleResponse.builder()
            .id(cycle.getId())
            .startDay(cycle.getStartDay())
            .endDay(cycle.getEndDay())
            .payday(cycle.getPayday())
            .active(cycle.getActive())
                .workingDays(cycle.getWorkingDays())
            .createdAt(cycle.getCreatedAt())
            .build();
    }
}
