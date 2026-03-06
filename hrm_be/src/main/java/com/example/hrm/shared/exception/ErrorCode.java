package com.example.hrm.shared.exception;

public enum ErrorCode {


    NOT_FOUND("E1000", "NOT FOUND"),
    // Employee errors (1000-1999)
    EMPLOYEE_NOT_FOUND("E1001", "Nhân viên không tìm thấy"),
    EMPLOYEE_CODE_ALREADY_EXISTS("E1002", "Mã nhân viên đã tồn tại"),
    EMPLOYEE_EMAIL_ALREADY_EXISTS("E1003", "Email nhân viên đã tồn tại"),
    EMPLOYEE_INVALID_STATUS("E1004", "Trạng thái nhân viên không hợp lệ"),

    // User Account errors (2000-2999)
    USER_NOT_FOUND("E2001", "Tài khoản người dùng không tìm thấy"),
    USER_USERNAME_ALREADY_EXISTS("E2002", "Tên đăng nhập đã tồn tại"),
    USER_INVALID_PASSWORD("E2003", "Mật khẩu không hợp lệ"),
    USER_ACCOUNT_LOCKED("E2004", "Tài khoản đã bị khóa"),
    USER_ACCOUNT_ALREADY_EXISTS("E2007", "nhân viên này đã có tài khoản rồi"),
    USER_UNAUTHORIZED("E2005", "Người dùng không có quyền thực hiện hành động này"),
    INVALID_USERNAME_OR_PASSWORD("E2006", "invalid user name or password"),
    USER_NOT_ACTIVE("E2008", "tài khoản không được kích hoạt hoặc sai tên và mật khẩu"),
    UNAUTHORIZED("E2009", "Chưa đăng nhập hoặc phiên làm việc đã hết hạn"),
    FORBIDDEN("E2010", "Bạn không có quyền thực hiện thao tác này"),

    // Department errors (3000-3999)
    DEPARTMENT_NOT_FOUND("E3001", "Phòng ban không tìm thấy"),
    DEPARTMENT_NAME_ALREADY_EXISTS("E3002", "Tên phòng ban đã tồn tại"),
    DEPARTMENT_HAS_EMPLOYEES("E3003", "Phòng ban không thể xóa vì còn nhân viên"),

    // SubDepartment errors (4000-4999)
    SUB_DEPARTMENT_NOT_FOUND("E4001", "Phòng ban con không tìm thấy"),
    SUB_DEPARTMENT_NAME_ALREADY_EXISTS("E4002", "Tên phòng ban con đã tồn tại trong phòng ban này"),
    SUB_DEPARTMENT_DEPARTMENT_NOT_FOUND("E4003", "Phòng ban cha không tồn tại"),

    // Contract errors (5000-5999)
    CONTRACT_NOT_FOUND("E5001", "Hợp đồng không tìm thấy"),
    CONTRACT_CODE_ALREADY_EXISTS("E5002", "Mã hợp đồng đã tồn tại"),
    CONTRACT_EMPLOYEE_NOT_FOUND("E5003", "Nhân viên không tồn tại"),
    CONTRACT_INVALID_DATE_RANGE("E5004", "Ngày kết thúc phải sau ngày bắt đầu"),
    CONTRACT_INVALID_STATUS("E5005", "Trạng thái hợp đồng không hợp lệ"),

    // Salary Contract errors (6000-6999)
    SALARY_CONTRACT_NOT_FOUND("E6001", "Hợp đồng lương không tìm thấy"),
    SALARY_CONTRACT_INVALID_DATE("E6002", "Ngày hiệu lực không hợp lệ"),
    SALARY_CONTRACT_NEGATIVE_AMOUNT("E6003", "Số tiền lương không được âm"),

    // Attendance errors (7000-7999)
    ATTENDANCE_NOT_FOUND("E7001", "Bản ghi chấm công không tìm thấy"),
    ATTENDANCE_DUPLICATE_DATE("E7002", "Chấm công cho ngày này đã tồn tại"),
    ATTENDANCE_INVALID_TIME("E7003", "Thời gian chấm công không hợp lệ"),

    // Leave errors (8000-8999)
    LEAVE_NOT_FOUND("E8001", "Yêu cầu nghỉ không tìm thấy"),
    LEAVE_INVALID_DATE_RANGE("E8002", "Ngày kết thúc phải sau ngày bắt đầu"),
    LEAVE_INSUFFICIENT_DAYS("E8003", "Số ngày nghỉ không đủ"),
    LEAVE_INVALID_STATUS("E8004", "Trạng thái yêu cầu nghỉ không hợp lệ"),
    LEAVE_OVERLAPPING_DATES("E8005", "Yêu cầu nghỉ trùng với yêu cầu khác"),

    // Payroll errors (9000-9999)
    PAYROLL_NOT_FOUND("E9001", "Bảng lương không tìm thấy"),
    PAYROLL_ALREADY_EXISTS("E9002", "Bảng lương cho tháng này đã tồn tại"),
    PAYROLL_INVALID_MONTH_FORMAT("E9003", "Định dạng tháng không hợp lệ (yyyy-MM)"),
    PAYROLL_INVALID_STATUS("E9004", "Trạng thái bảng lương không hợp lệ"),
    PAYROLL_CANNOT_MODIFY_APPROVED("E9005", "Không thể sửa bảng lương đã phê duyệt"),

    // Payroll Approval History errors (10000-10999)
    PAYROLL_APPROVAL_NOT_FOUND("E10001", "Lịch sử phê duyệt lương không tìm thấy"),
    PAYROLL_APPROVAL_INVALID_STATUS("E10002", "Trạng thái phê duyệt không hợp lệ"),

    // OT Rate errors (11000-11999)
    OT_RATE_NOT_FOUND("E11001", "Mức tăng ca không tìm thấy"),
    OT_RATE_INVALID_RATE("E11002", "Mức tăng ca phải lớn hơn 0"),
    OT_RATE_INVALID_TYPE("E11003", "Loại tăng ca không hợp lệ"),
    OT_RATE_DUPLICATE("E11004", "OT rate for this date and type already exists"),

    // Salary Adjustment errors (12000-12999)
    SALARY_ADJUSTMENT_NOT_FOUND("E12001", "Điều chỉnh lương không tìm thấy"),
    SALARY_ADJUSTMENT_NEGATIVE_AMOUNT("E12002", "Số tiền điều chỉnh không được âm"),
    SALARY_ADJUSTMENT_INVALID_MONTH("E12003", "Tháng điều chỉnh không hợp lệ"),

    // Address errors (13000-13999)
    ADDRESS_NOT_FOUND("E13001", "Địa chỉ không tìm thấy"),
    ADDRESS_WARD_NOT_FOUND("E13002", "Phường/xã không tồn tại"),

    // Location (Province/District/Ward) errors (14000-14999)
    PROVINCE_NOT_FOUND("E14001", "Tỉnh/thành phố không tìm thấy"),
    DISTRICT_NOT_FOUND("E14002", "Quận/huyện không tìm thấy"),
    WARD_NOT_FOUND("E14003", "Phường/xã không tìm thấy"),

    // Role & Permission errors (15000-15999)
    ROLE_NOT_FOUND("E15001", "Vai trò không tìm thấy"),
    ROLE_NAME_ALREADY_EXISTS("E15002", "Tên vai trò đã tồn tại"),
    PERMISSION_NOT_FOUND("E15003", "Quyền không tìm thấy"),
    PERMISSION_NAME_ALREADY_EXISTS("E15004", "Tên quyền đã tồn tại"),

    // File attachment errors (16000-16999)
    FILE_NOT_FOUND("E16001", "Tệp đính kèm không tìm thấy"),
    FILE_UPLOAD_FAILED("E16002", "Tải lên tệp thất bại"),
    FILE_SIZE_EXCEEDED("E16003", "Kích thước tệp vượt quá giới hạn cho phép"),
    FILE_INVALID_TYPE("E16004", "Loại tệp không được hỗ trợ"),
    FILE_IS_EMPTY("E16005", "không có file nào tồn tại trong request"),
    HANDLING_FAILED_FILES("E16006", "xử lý file thất bại"),
    FILE_ALREADY_EXISTS("E16007", "File đã tồn tại"),

    // Validation errors (20000-20999)
    VALIDATION_ERROR("E20001", "Dữ liệu đầu vào không hợp lệ"),
    INVALID_INPUT("E20002", "Đầu vào không hợp lệ"),
    MISSING_REQUIRED_FIELD("E20003", "Trường bắt buộc không được để trống"),
    INVALID_DATE_FORMAT("E20004", "Định dạng ngày không hợp lệ"),
    INVALID_EMAIL_FORMAT("E20005", "Định dạng email không hợp lệ"),

    // Database errors (30000-30999)
    DATABASE_ERROR("E30001", "Lỗi cơ sở dữ liệu"),
    TRANSACTION_FAILED("E30002", "Giao dịch thất bại"),
    CONSTRAINT_VIOLATION("E30003", "Vi phạm ràng buộc cơ sở dữ liệu"),

    // Server errors (40000-40999)
    INTERNAL_SERVER_ERROR("E40001", "Lỗi máy chủ nội bộ"),
    SERVICE_UNAVAILABLE("E40002", "Dịch vụ không khả dụng"),
    TIMEOUT_ERROR("E40003", "Hết thời gian chờ"),

    //Server errors (50000-50999)
    CONTACT_NOT_FOUND("E50001", "không tồn tại contact"),

    //Token errors(60000- 60999)
    INVALID_TOKEN_TYPE("E600001","invalid token type"),
    INVALID_TOKEN("E600002", "invalid token"),
    TOKEN_HAS_EXPIRED("E600003", "token has expired"),

    //breaktime error(70000 - 70999)
    IN_BREAK_TIME("E70001", "in break time"),

    // Payroll Cycle errors (90000-90999)
    PAYROLL_CYCLE_NOT_FOUND("E8006", "Chưa cấu hình chu kỳ lương"),


    // ALLOWANCE errors (90000-900020)
    ALLOWANCE_NOT_FOUND("E90001", "Phụ cấp không tìm thấy"),
    ALLOWANCE_CODE_EXISTS("E900001", "ALLOWANCE CODE EXISTS"),
    // Penalty Rule errors (17000-17999)
    PENALTY_RULE_NOT_FOUND("E17001", "Quy tắc phạt không tìm thấy"),
    PENALTY_RULE_CODE_ALREADY_EXISTS("E17002", "Mã quy tắc phạt đã tồn tại"),
    PENALTY_RULE_INVALID_VALUE("E17003", "Giá trị quy tắc phạt không hợp lệ"),

    PERMISSION_DENIED("E99995", "PERMISSION_DENIED"),

    NOT_ENOUGH_LEAVE("E99996", "NOT_ENOUGH_LEAVE"),

    INVALID_STATE("E99997", "INVALID_STATE"),

    DUPLICATE_CODE("E99998", "DUPLICATE"),
    // Unknown error
    UNKNOWN_ERROR("E99999", "Lỗi không xác định");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
