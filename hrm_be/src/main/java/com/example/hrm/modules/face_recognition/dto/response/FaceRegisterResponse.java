package com.example.hrm.modules.face_recognition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceRegisterResponse {
    private String status;      // "success" / "error"
    private String message;     // thông báo
    private FaceData data;      // dữ liệu chi tiết
}
