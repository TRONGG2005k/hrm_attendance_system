package com.example.hrm.shared.excel;

import com.example.hrm.shared.enums.*;
import com.example.hrm.shared.mapper.EnumMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExcelHelper {

    private final EnumMapper enumMapper;

    private final DataFormatter formatter = new DataFormatter();

    public String getString(Cell cell) {
        if (cell == null)
            return null;
        String value = formatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }

    public LocalDate getLocalDate(Cell cell) {
        if (cell == null)
            return null;

        // Nếu là kiểu Date trong Excel
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        // Nếu là text
        String value = getString(cell);
        if (value == null)
            return null;

        // Thử các format phổ biến
        try {
            return LocalDate.parse(value); // yyyy-MM-dd
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(value, formatter);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public ShiftType getShiftType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapShiftType(value);
    }

    public Gender getGender(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapGender(value);
    }

    public EmployeeStatus getEmployeeStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapEmployeeStatus(value);
    }

    public AdjustmentType getAdjustmentType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapAdjustmentType(value);
    }

    public AllowanceCalculationType getAllowanceCalculationType(Cell cell) {
        String value = getString(cell);
        log.warn("value : {}", value);
        if (value == null)
            return null;
        return enumMapper.mapAllowanceCalculationType(value);
    }

    public AttendanceEvaluation getAttendanceEvaluation(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapAttendanceEvaluation(value);
    }

    public AttendanceStatus getAttendanceStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapAttendanceStatus(value);
    }

    public BasedOn getBasedOn(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapBasedOn(value);
    }

    public BreakType getBreakType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapBreakType(value);
    }

    public ContactType getContactType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapContactType(value);
    }

    public ContractStatus getContractStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapContractStatus(value);
    }

    public ContractType getContractType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapContractType(value);
    }

    public LeaveStatus getLeaveStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapLeaveStatus(value);
    }

    public LeaveType getLeaveType(Cell cell) {
        String value = getString(cell);

        if (value == null)
            return null;
        log.info("Raw value='{}', length={}", value, value.length());

        return enumMapper.mapLeaveType(value);
    }

    public OTType getOTType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapOTType(value);
    }

    public PayrollApprovalStatus getPayrollApprovalStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapPayrollApprovalStatus(value);
    }

    public PayrollStatus getPayrollStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapPayrollStatus(value);
    }

    public PenaltyType getPenaltyType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapPenaltyType(value);
    }

    public RefType getRefType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapRefType(value);
    }

    public SalaryContractStatus getSalaryContractStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapSalaryContractStatus(value);
    }

    public TokenType getTokenType(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapTokenType(value);
    }

    public UserStatus getUserStatus(Cell cell) {
        String value = getString(cell);
        if (value == null)
            return null;
        return enumMapper.mapUserStatus(value);
    }

    public Integer getInteger(Cell cell) {
        if (cell == null)
            return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }

        String value = getString(cell);
        if (value == null)
            return null;

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    public Double getDouble(Cell cell) {
        if (cell == null)
            return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        String value = getString(cell);
        if (value == null)
            return null;

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }

    public BigDecimal getBigDecimal(Cell cell) {
        if (cell == null)
            return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        String value = getString(cell);
        if (value == null)
            return null;

        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean getBoolean(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }

        String value = getString(cell);
        if (value == null) {
            return null;
        }

        value = value.toLowerCase();
        if (value.equals("true") || value.equals("1") || value.equals("yes") || value.equals("y")) {
            return true;
        }
        if (value.equals("false") || value.equals("0") || value.equals("no") || value.equals("n")) {
            return false;
        }

        return null;
    }

    // ================== VALIDATE ==================

    public boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean isValidEmail(String email) {
        if (isBlank(email))
            return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public boolean isValidPhone(String phone) {
        if (isBlank(phone))
            return false;
        return phone.matches("^0[0-9]{9}$"); // Việt Nam: 10 số bắt đầu bằng 0
    }

    public <T extends Enum<T>> boolean isValidEnum(Class<T> enumClass, String value) {
        if (isBlank(value))
            return false;
        try {
            Enum.valueOf(enumClass, value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    public boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    public boolean isValidDateFormat(String value) {
        if (isBlank(value))
            return false;
        try {
            LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPositiveNumber(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    public boolean isNonNegativeNumber(Number number) {
        return number != null && number.doubleValue() >= 0;
    }

    public boolean isLengthBetween(String value, int min, int max) {
        if (value == null)
            return false;
        int len = value.length();
        return len >= min && len <= max;
    }
}
