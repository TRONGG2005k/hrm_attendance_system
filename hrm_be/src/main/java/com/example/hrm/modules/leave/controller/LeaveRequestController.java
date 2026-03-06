package com.example.hrm.modules.leave.controller;

import com.example.hrm.modules.leave.dto.request.LeaveRequestApprovalRequest;
import com.example.hrm.modules.leave.dto.request.LeaveRequestCreateRequest;
import com.example.hrm.modules.leave.dto.response.LeaveRequestDetailResponse;
import com.example.hrm.modules.leave.dto.response.LeaveRequestListItemResponse;
import com.example.hrm.modules.leave.excel.LeaveRequestExcelService;
import com.example.hrm.modules.leave.service.LeaveRequestService;
import com.example.hrm.shared.ExcelResult;
import com.example.hrm.shared.enums.LeaveStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("${app.api-prefix}/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final LeaveRequestExcelService leaveRequestExcelService;

    /**
     * Tạo yêu cầu nghỉ phép mới
     * @param request Thông tin yêu cầu nghỉ phép
     * @return LeaveRequestDetailResponse chi tiết yêu cầu nghỉ phép
     */
    @PostMapping
    public ResponseEntity<LeaveRequestDetailResponse> create(@Valid @RequestBody LeaveRequestCreateRequest request) {
        LeaveRequestDetailResponse response = leaveRequestService.create(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật yêu cầu nghỉ phép
     * @param leaveId ID của yêu cầu nghỉ phép
     * @param request Thông tin cập nhật
     * @return LeaveRequestDetailResponse chi tiết yêu cầu nghỉ phép sau khi cập nhật
     */
    @PutMapping("/{leaveId}")
    public ResponseEntity<LeaveRequestDetailResponse> update(
            @PathVariable String leaveId,
            @Valid @RequestBody LeaveRequestCreateRequest request
    ) {
        LeaveRequestDetailResponse response = leaveRequestService.update(request, leaveId);
        return ResponseEntity.ok(response);
    }

    /**
     * Duyệt hoặc từ chối yêu cầu nghỉ phép
     * @param leaveId ID của yêu cầu nghỉ phép
     * @param request Thông tin duyệt yêu cầu
     * @return LeaveRequestDetailResponse chi tiết yêu cầu nghỉ phép sau khi duyệt
     */
    @PostMapping("/{leaveId}/approve")
    public ResponseEntity<LeaveRequestDetailResponse> approveLeave(
            @PathVariable String leaveId,
            @Valid @RequestBody LeaveRequestApprovalRequest request
    ) {
        LeaveRequestDetailResponse response = leaveRequestService.approveLeave(request, leaveId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy chi tiết yêu cầu nghỉ phép theo ID
     * @param leaveId ID của yêu cầu nghỉ phép
     * @return LeaveRequestDetailResponse chi tiết yêu cầu nghỉ phép
     */
    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveRequestDetailResponse> getById(@PathVariable String leaveId) {
        LeaveRequestDetailResponse response = leaveRequestService.getById(leaveId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách yêu cầu nghỉ phép (phân trang)
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param size Số bản ghi mỗi trang
     * @return Page<LeaveRequestListItemResponse> danh sách yêu cầu nghỉ phép
     */
    @GetMapping
    public ResponseEntity<Page<LeaveRequestListItemResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<LeaveRequestListItemResponse> response = leaveRequestService.getAll(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách yêu cầu nghỉ phép theo trạng thái (phân trang)
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param size Số bản ghi mỗi trang
     * @param status Trạng thái yêu cầu nghỉ phép
     * @return Page<LeaveRequestListItemResponse> danh sách yêu cầu nghỉ phép
     */
    @GetMapping("/list")
    public ResponseEntity<Page<LeaveRequestListItemResponse>> getAllByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam LeaveStatus status
    ) {
        Page<LeaveRequestListItemResponse> response = leaveRequestService.getAllByStatus(page, size, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Xuất danh sách yêu cầu nghỉ phép ra file Excel theo phòng ban con
     * @param subDepartmentId ID của phòng ban con
     * @return File Excel chứa danh sách yêu cầu nghỉ phép
     */
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportLeaveRequests(@RequestParam String subDepartmentId) {
        ByteArrayInputStream in = leaveRequestExcelService.exportLeaveRequestsToExcel(subDepartmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=leave_requests.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    /**
     * Import danh sách yêu cầu nghỉ phép từ file Excel
     * @param file File Excel chứa danh sách yêu cầu nghỉ phép
     * @return Kết quả import
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExcelResult> importLeaveRequests(@RequestParam("file") MultipartFile file) {
        ExcelResult result = leaveRequestExcelService.importExcel(file);
        return ResponseEntity.ok(result);
    }
}
