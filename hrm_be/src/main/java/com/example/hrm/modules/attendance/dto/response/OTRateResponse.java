package com.example.hrm.modules.attendance.dto.response;

import com.example.hrm.shared.enums.OTType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OTRateResponse {

    String id;

    LocalDate date;

    OTType type;

    Double rate;

    String description;

}
