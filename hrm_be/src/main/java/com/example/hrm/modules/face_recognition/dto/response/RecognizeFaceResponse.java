package com.example.hrm.modules.face_recognition.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecognizeFaceResponse {
    String name;
    String code;
    LocalDateTime time;
}
