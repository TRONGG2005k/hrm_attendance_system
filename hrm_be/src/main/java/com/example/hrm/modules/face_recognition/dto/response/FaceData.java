package com.example.hrm.modules.face_recognition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class FaceData {
    private String employeeId;
    private List<FaceInfo> faces;  // danh sách face đã đăng ký
}