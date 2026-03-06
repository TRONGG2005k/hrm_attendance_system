package com.example.hrm.modules.leave.dto.response;

import com.example.hrm.shared.enums.LeaveStatus;
import com.example.hrm.shared.enums.LeaveType;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Setter
public class LeaveRequestListItemResponse {
    private  String id;
    private  String employeeId;
    private  String employeeCode;
    private  String fullName;
    private  LocalDate startDate;
    private  LocalDate endDate;
    private  LeaveType type;
    private  LeaveStatus status;


}
