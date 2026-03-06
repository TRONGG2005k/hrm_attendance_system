package com.example.hrm.modules.user.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivateAccountRequest {
     String token;      // Activation token
     String password;   // Password mới
}
