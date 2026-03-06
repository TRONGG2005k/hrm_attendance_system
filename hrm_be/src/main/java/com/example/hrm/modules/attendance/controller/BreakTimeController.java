package com.example.hrm.modules.attendance.controller;

import com.example.hrm.modules.attendance.dto.request.BreakTimeBatchRequest;
import com.example.hrm.modules.attendance.dto.request.BreakTimeRequest;
import com.example.hrm.modules.attendance.dto.response.BreakTimeResponse;
import com.example.hrm.modules.attendance.service.BreakTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/breaks")
@RequiredArgsConstructor
public class BreakTimeController {

    private final BreakTimeService breakTimeService;

    @PostMapping("/batch")
    public List<String> createBreakForSubDepartment(@RequestBody BreakTimeBatchRequest request) {
        return breakTimeService.updateBreakForSubDepartment(request);
    }

    // Tạo break
    @PostMapping
    public BreakTimeResponse createBreak(@RequestBody BreakTimeRequest request) {
        return breakTimeService.createBreak(request);
    }

    // Cập nhật break
    @PutMapping("/{id}")
    public BreakTimeResponse updateBreak(@PathVariable String id, @RequestBody BreakTimeRequest request) {
        return breakTimeService.updateBreak(id, request);
    }

    // Xóa break
    @DeleteMapping("/{id}")
    public void deleteBreak(@PathVariable String id) {
        breakTimeService.deleteBreak(id);
    }

    // Lấy break theo attendance (paging)
    @GetMapping("/attendance/{attendanceId}")
    public Page<BreakTimeResponse> getBreaksByAttendance(@PathVariable String attendanceId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return breakTimeService.getBreaksByAttendance(attendanceId, PageRequest.of(page, size));
    }
}
