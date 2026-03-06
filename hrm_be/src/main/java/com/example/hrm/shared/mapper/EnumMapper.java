package com.example.hrm.shared.mapper;

import com.example.hrm.shared.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mapper cho các enum từ chuỗi tiếng Việt/tiếng Anh
 */
@Component
@Slf4j
public class EnumMapper {

    public Gender mapGender(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "nam", "male" -> Gender.MALE;
            case "nữ", "nu", "female" -> Gender.FEMALE;
            case "khác", "other" -> Gender.OTHER;
            default -> {
                log.warn("Unknown gender value: {}", value);
                yield null;
            }
        };
    }

    public EmployeeStatus mapEmployeeStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đang làm", "dang lam", "active" -> EmployeeStatus.ACTIVE;
            case "nghỉ việc", "nghi viec", "inactive", "resigned" -> EmployeeStatus.INACTIVE;
            case "đang nghỉ", "dang nghi", "on_leave" -> EmployeeStatus.ON_LEAVE;
            default -> {
                log.warn("Unknown employee status value: {}", value);
                yield null;
            }
        };
    }

    public ShiftType mapShiftType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "ca sáng", "ca sang", "morning", "full-time" -> ShiftType.MORNING;
            case "ca tối", "ca toi", "night", "part-time" -> ShiftType.NIGHT;
            default -> {
                log.warn("Unknown shift type value: {}", value);
                yield null;
            }
        };
    }
    public FileCategory mapFileCategory(String value) {

        if (value == null || value.trim().isEmpty()) {
            return FileCategory.OTHER;
        }

        String normalized = value
                .trim()
                .toLowerCase()
                .replace("-", "_")
                .replace(" ", "_");

        return switch (normalized) {

            // CONTRACT
            case "contract", "hop_dong", "hopdong" ->
                    FileCategory.CONTRACT;

            // CV
            case "cv", "resume", "so_yeu_ly_lich", "soyeulylich" ->
                    FileCategory.CV;

            // CERTIFICATE
            case "certificate", "cert", "bang_cap", "bangcap", "chung_chi", "chungchi" ->
                    FileCategory.CERTIFICATE;

            // ID CARD
            case "id_card", "cmnd", "cccd", "identity", "can_cuoc", "cancuoc" ->
                    FileCategory.ID_CARD;

            // AVATAR
            case "avatar", "profile", "profile_picture", "anh_dai_dien", "anhdaidien" ->
                    FileCategory.AVATAR;

            // OTHER
            case "other", "khac", "file_khac", "filekhac" ->
                    FileCategory.OTHER;

            default -> {
                log.warn("Unknown file category value: {}", value);
                yield FileCategory.OTHER;
            }
        };
    }

    public AdjustmentType mapAdjustmentType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "thưởng", "bonus" -> AdjustmentType.BONUS;
            case "phạt", "penalty" -> AdjustmentType.PENALTY;
            case "tăng ca", "overtime" -> AdjustmentType.OVERTIME;
            case "phụ cấp", "allowance" -> AdjustmentType.ALLOWANCE;
            case "nghỉ không lương", "absence_deduct", "absence deduct" -> AdjustmentType.ABSENCE_DEDUCT;
            default -> {
                log.warn("Unknown adjustment type value: {}", value);
                yield null;
            }
        };
    }

    public AllowanceCalculationType mapAllowanceCalculationType(String value) {
        log.warn("value : {}", value);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "cố định", "fixed" -> AllowanceCalculationType.FIXED;
            case "theo ngày", "per_day", "per day" -> AllowanceCalculationType.PER_DAY;
            case "theo ngày công chuẩn", "per_working_day", "per working day" -> AllowanceCalculationType.PER_WORKING_DAY;
            case "theo giờ ot", "per_ot_hour", "per ot hour" -> AllowanceCalculationType.PER_OT_HOUR;
            default -> {
                log.warn("Unknown allowance calculation type value: {}", value);
                yield null;
            }
        };
    }

    public AttendanceEvaluation mapAttendanceEvaluation(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đi muộn", "late" -> AttendanceEvaluation.LATE;
            case "về sớm", "leave_early", "leave early" -> AttendanceEvaluation.LEAVE_EARLY;
            case "đúng giờ", "on_time", "on time" -> AttendanceEvaluation.ON_TIME;
            case "tăng ca", "over_time", "over time" -> AttendanceEvaluation.OVER_TIME;
            case "nghỉ có phép", "leave_approved", "leave approved" -> AttendanceEvaluation.LEAVE_APPROVED;
            default -> {
                log.warn("Unknown attendance evaluation value: {}", value);
                yield null;
            }
        };
    }

    public AttendanceStatus mapAttendanceStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đang làm việc", "working" -> AttendanceStatus.WORKING;
            case "đã hoàn thành", "completed" -> AttendanceStatus.COMPLETED;
            case "nghỉ phép", "leave" -> AttendanceStatus.LEAVE;
            case "vắng", "absent" -> AttendanceStatus.ABSENT;
            default -> {
                log.warn("Unknown attendance status value: {}", value);
                yield null;
            }
        };
    }

    public BasedOn mapBasedOn(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "phút", "minute" -> BasedOn.MINUTE;
            case "ngày", "day" -> BasedOn.DAY;
            case "lần", "time" -> BasedOn.TIME;
            case "cố định", "fixed" -> BasedOn.FIXED;
            case "phút đi muộn", "late_minutes", "late minutes" -> BasedOn.LATE_MINUTES;
            case "phút về sớm", "early_leave_minutes", "early leave minutes" -> BasedOn.EARLY_LEAVE_MINUTES;
            case "hủy lương cơ bản", "void_base_salary", "void base salary" -> BasedOn.VOID_BASE_SALARY;
            case "hủy tăng ca", "void_overtime", "void overtime" -> BasedOn.VOID_OVERTIME;
            default -> {
                log.warn("Unknown based on value: {}", value);
                yield null;
            }
        };
    }

    public BreakType mapBreakType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "ăn trưa", "lunch" -> BreakType.LUNCH;
            case "nghỉ ngơi", "rest" -> BreakType.REST;
            case "cá nhân", "personal" -> BreakType.PERSONAL;
            default -> {
                log.warn("Unknown break type value: {}", value);
                yield null;
            }
        };
    }

    public ContactType mapContactType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "email" -> ContactType.EMAIL;
            case "điện thoại", "phone" -> ContactType.PHONE;
            case "zalo" -> ContactType.ZALO;
            case "linkedin" -> ContactType.LINKEDIN;
            case "facebook" -> ContactType.FACEBOOK;
            case "khác", "other" -> ContactType.OTHER;
            default -> {
                log.warn("Unknown contact type value: {}", value);
                yield null;
            }
        };
    }

    public ContractStatus mapContractStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đang hoạt động", "active" -> ContractStatus.ACTIVE;
            case "đã hết hạn", "expired" -> ContractStatus.EXPIRED;
            case "đã chấm dứt", "terminated" -> ContractStatus.TERMINATED;
            default -> {
                log.warn("Unknown contract status value: {}", value);
                yield null;
            }
        };
    }

    public ContractType mapContractType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "thử việc", "probation" -> ContractType.PROBATION;
            case "chính thức", "fulltime" -> ContractType.FULLTIME;
            case "theo mùa", "seasonal" -> ContractType.SEASONAL;
            default -> {
                log.warn("Unknown contract type value: {}", value);
                yield null;
            }
        };
    }

    public LeaveStatus mapLeaveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "chờ duyệt", "pending" -> LeaveStatus.PENDING;
            case "đã duyệt", "approved" -> LeaveStatus.APPROVED;
            case "từ chối", "rejected" -> LeaveStatus.REJECTED;
            case "hủy", "cancelled" -> LeaveStatus.CANCELLED;
            default -> {
                log.warn("Unknown leave status value: {}", value);
                yield null;
            }
        };
    }

    public LeaveType mapLeaveType(String value) {
        log.info("Raw value='{}', length={}", value, value.length());

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "nghỉ năm", "annual" -> LeaveType.ANNUAL;
            case "nghỉ ốm", "sick" -> LeaveType.SICK;
            case "nghỉ không lương", "unpaid" -> LeaveType.UNPAID;
            case "nghỉ thai sản", "maternity" -> LeaveType.MATERNITY;
            case "nghỉ tang", "bereavement" -> LeaveType.BEREAVEMENT;
            default -> {
                log.warn("Unknown leave type value: {}", value);
                yield null;
            }
        };
    }

    public OTType mapOTType(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "thường", "normal" -> OTType.NORMAL;
            case "chủ nhật", "sunday" -> OTType.SUNDAY;
            case "ngày lễ", "holiday" -> OTType.HOLIDAY;
            case "đêm", "night" -> OTType.NIGHT;
            default -> {
                log.warn("Unknown OT type value: {}", value);
                yield null;
            }
        };
    }

    public PayrollApprovalStatus mapPayrollApprovalStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đã duyệt", "approved" -> PayrollApprovalStatus.APPROVED;
            case "từ chối", "rejected" -> PayrollApprovalStatus.REJECTED;
            default -> {
                log.warn("Unknown payroll approval status value: {}", value);
                yield null;
            }
        };
    }

    public PayrollStatus mapPayrollStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "nháp", "draft" -> PayrollStatus.DRAFT;
            case "chờ duyệt", "pending_approval", "pending approval" -> PayrollStatus.PENDING_APPROVAL;
            case "đã duyệt", "approved" -> PayrollStatus.APPROVED;
            case "từ chối", "rejected" -> PayrollStatus.REJECTED;
            case "đã trả", "paid" -> PayrollStatus.PAID;
            default -> {
                log.warn("Unknown payroll status value: {}", value);
                yield null;
            }
        };
    }

    public PenaltyType mapPenaltyType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "phút", "minutes" -> PenaltyType.MINUTES;
            case "ngày", "day" -> PenaltyType.DAY;
            case "tiền", "money" -> PenaltyType.MONEY;
            case "lần", "time" -> PenaltyType.TIME;
            case "cố định", "fixed" -> PenaltyType.FIXED;
            case "phần trăm", "percent" -> PenaltyType.PERCENT;
            case "mỗi phút", "per_minute", "per minute" -> PenaltyType.PER_MINUTE;
            case "đi muộn", "late" -> PenaltyType.LATE;
            case "về sớm", "early_leave", "early leave" -> PenaltyType.EARLY_LEAVE;
            case "vắng không phép", "absent_no_permission", "absent no permission" -> PenaltyType.ABSENT_NO_PERMISSION;
            case "không checkout", "no_checkout", "no checkout" -> PenaltyType.NO_CHECKOUT;
            case "hủy lương cơ bản", "void_base_salary", "void base salary" -> PenaltyType.VOID_BASE_SALARY;
            case "hủy tăng ca", "void_overtime", "void overtime" -> PenaltyType.VOID_OVERTIME;
            default -> {
                log.warn("Unknown penalty type value: {}", value);
                yield null;
            }
        };
    }

    public RefType mapRefType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "nhân viên", "employee" -> RefType.EMPLOYEE;
            case "hợp đồng", "contract" -> RefType.CONTRACT;
            case "dự án", "project" -> RefType.PROJECT;
            default -> {
                log.warn("Unknown ref type value: {}", value);
                yield null;
            }
        };
    }

    public SalaryContractStatus mapSalaryContractStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đang hoạt động", "active" -> SalaryContractStatus.ACTIVE;
            case "đã hết hạn", "expired" -> SalaryContractStatus.EXPIRED;
            case "không hoạt động", "inactive" -> SalaryContractStatus.INACTIVE;
            default -> {
                log.warn("Unknown salary contract status value: {}", value);
                yield null;
            }
        };
    }

    public TokenType mapTokenType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "truy cập", "access" -> TokenType.ACCESS;
            case "làm mới", "refresh" -> TokenType.REFRESH;
            case "kích hoạt", "activation" -> TokenType.ACTIVATION;
            default -> {
                log.warn("Unknown token type value: {}", value);
                yield null;
            }
        };
    }

    public UserStatus mapUserStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đang hoạt động", "active" -> UserStatus.ACTIVE;
            case "không hoạt động", "not_active", "not active" -> UserStatus.NOT_ACTIVE;
            case "chờ kích hoạt", "pending_active", "pending active" -> UserStatus.PENDING_ACTIVE;
            default -> {
                log.warn("Unknown user status value: {}", value);
                yield null;
            }
        };
    }
}
