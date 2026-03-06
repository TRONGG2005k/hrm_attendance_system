package com.example.hrm.modules.face_recognition.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceRecognizeResponse {

    @JsonProperty("employee_id")
    private String employeeId;

}
