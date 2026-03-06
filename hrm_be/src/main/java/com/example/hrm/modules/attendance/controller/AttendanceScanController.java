package com.example.hrm.modules.attendance.controller;

import com.example.hrm.modules.attendance.dto.response.AttendanceRealTimeResponse;
import com.example.hrm.modules.attendance.service.AttendanceScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${app.api-prefix}/attendance")
@RequiredArgsConstructor
public class AttendanceScanController {

    private final AttendanceScanService attendanceService;

    /**
     * Endpoint quét khuôn mặt để check-in/check-out
     * @param file hình ảnh quét khuôn mặt
     * @return thông tin check-in/check-out
     */
    @PostMapping("/scan")
    public ResponseEntity<AttendanceRealTimeResponse> scanFace(@RequestParam("file") MultipartFile file) {
        AttendanceRealTimeResponse response = attendanceService.scan(file);
        if (response == null) {
            // Trường hợp trong giờ nghỉ
            return ResponseEntity.status(HttpStatus.OK)
                    .body(AttendanceRealTimeResponse.builder()
                            .message("Hiện tại đang trong giờ nghỉ, không tính check-out")
                            .build());
        }
        return ResponseEntity.ok(response);
    }
}
