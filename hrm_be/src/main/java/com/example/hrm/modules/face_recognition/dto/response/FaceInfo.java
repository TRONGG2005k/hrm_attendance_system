package com.example.hrm.modules.face_recognition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

class FaceInfo {
    private String faceId;
    private LocalDateTime registeredAt;
}
