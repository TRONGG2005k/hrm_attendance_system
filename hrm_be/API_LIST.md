# 📋 DANH SÁCH TOÀN BỘ API HỆ THỐNG HRM

**Tổng số API: 141 endpoints**

---

## 🔐 MODULE: AUTH (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 1 | POST /api/v1/auth/login | Đăng nhập hệ thống | Auth |
| 2 | DELETE /api/v1/auth/logout | Đăng xuất (xóa refresh token) | Auth |
| 3 | DELETE /api/v1/auth/logoutAll | Đăng xuất tất cả thiết bị | Auth |
| 4 | POST /api/v1/auth/refresh | Làm mới access token | Auth |
| 5 | POST /api/v1/auth/activate | Kích hoạt tài khoản ngườii dùng | Auth |

---

## 👤 MODULE: USER MANAGEMENT (11 APIs)

### User Account Controller (6 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 6 | POST /api/v1/user-accounts | Tạo tài khoản ngườii dùng thủ công | User |
| 7 | POST /api/v1/user-accounts/auto/{id} | Tạo tài khoản tự động từ employee ID | User |
| 8 | GET /api/v1/user-accounts | Lấy danh sách tài khoản (phân trang) | User |
| 9 | GET /api/v1/user-accounts/{id} | Lấy chi tiết tài khoản theo ID | User |
| 10 | PUT /api/v1/user-accounts/{id} | Cập nhật tài khoản ngườii dùng | User |
| 11 | DELETE /api/v1/user-accounts/{id} | Xóa tài khoản ngườii dùng | User |

### Role Controller (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 12 | POST /api/v1/roles | Tạo vai trò mới | User |
| 13 | GET /api/v1/roles | Lấy danh sách vai trò (phân trang) | User |
| 14 | GET /api/v1/roles/{id} | Lấy chi tiết vai trò | User |
| 15 | PUT /api/v1/roles/{id} | Cập nhật vai trò | User |
| 16 | DELETE /api/v1/roles/{id} | Xóa vai trò | User |

---

## 👨‍💼 MODULE: EMPLOYEE (30 APIs)

### Employee Controller (9 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 17 | GET /api/v1/employees | Lấy danh sách nhân viên (phân trang) | Employee |
| 18 | GET /api/v1/employees/{id} | Lấy chi tiết nhân viên | Employee |
| 19 | POST /api/v1/employees | Tạo nhân viên mới | Employee |
| 20 | PUT /api/v1/employees/{id} | Cập nhật thông tin nhân viên | Employee |
| 21 | DELETE /api/v1/employees/{id} | Xóa mềm nhân viên | Employee |
| 22 | POST /api/v1/employees/import | Import nhân viên từ Excel | Employee |
| 23 | POST /api/v1/employees/import-or-update | Import hoặc cập nhật từ Excel | Employee |
| 24 | POST /api/v1/employees/import-files | Import file đính kèm (zip) | Employee |
| 25 | GET /api/v1/employees/export | Export danh sách nhân viên ra Excel | Employee |

### Face Recognition Controller (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 26 | POST /api/v1/employees/{employeeId}/faces | Đăng ký khuôn mặt cho nhân viên | Face Recognition |
| 27 | POST /api/v1/employees/faces/recognize | Nhận diện khuôn mặt | Face Recognition |
| 28 | PUT /api/v1/employees/{employeeId}/faces | Cập nhật khuôn mặt | Face Recognition |
| 29 | DELETE /api/v1/employees/{employeeId}/faces | Xóa khuôn mặt | Face Recognition |
| 30 | POST /api/v1/employees/faces/batch | Đăng ký khuôn mặt hàng loạt | Face Recognition |

### Province Controller (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 31 | POST /api/v1/provinces | Tạo tỉnh/thành phố | Employee |
| 32 | GET /api/v1/provinces | Lấy danh sách tỉnh/thành (phân trang) | Employee |
| 33 | GET /api/v1/provinces/{id} | Lấy chi tiết tỉnh/thành | Employee |
| 34 | PUT /api/v1/provinces/{id} | Cập nhật tỉnh/thành | Employee |
| 35 | DELETE /api/v1/provinces/{id} | Xóa tỉnh/thành | Employee |

### District Controller (6 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 36 | POST /api/v1/districts | Tạo quận/huyện | Employee |
| 37 | GET /api/v1/districts | Lấy danh sách quận/huyện (phân trang) | Employee |
| 38 | GET /api/v1/districts/province/{provinceId} | Lấy quận/huyện theo tỉnh | Employee |
| 39 | GET /api/v1/districts/{id} | Lấy chi tiết quận/huyện | Employee |
| 40 | PUT /api/v1/districts/{id} | Cập nhật quận/huyện | Employee |
| 41 | DELETE /api/v1/districts/{id} | Xóa quận/huyện | Employee |

### Ward Controller (6 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 42 | POST /api/v1/wards | Tạo phường/xã | Employee |
| 43 | GET /api/v1/wards | Lấy danh sách phường/xã (phân trang) | Employee |
| 44 | GET /api/v1/wards/district/{districtId} | Lấy phường/xã theo quận | Employee |
| 45 | GET /api/v1/wards/{id} | Lấy chi tiết phường/xã | Employee |
| 46 | PUT /api/v1/wards/{id} | Cập nhật phường/xã | Employee |
| 47 | DELETE /api/v1/wards/{id} | Xóa phường/xã | Employee |

### Contact Controller (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 48 | POST /api/v1/contacts | Tạo liên hệ mới | Employee |
| 49 | GET /api/v1/contacts | Lấy danh sách liên hệ (phân trang) | Employee |
| 50 | GET /api/v1/contacts/{id} | Lấy chi tiết liên hệ | Employee |
| 51 | PUT /api/v1/contacts/{id} | Cập nhật liên hệ | Employee |
| 52 | DELETE /api/v1/contacts/{id} | Xóa mềm liên hệ | Employee |

### Address Controller (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 53 | GET /api/v1/addresses | Lấy danh sách địa chỉ (phân trang) | Employee |
| 54 | GET /api/v1/addresses/{id} | Lấy chi tiết địa chỉ | Employee |
| 55 | POST /api/v1/addresses | Tạo địa chỉ mới | Employee |
| 56 | PUT /api/v1/addresses/{id} | Cập nhật địa chỉ | Employee |
| 57 | DELETE /api/v1/addresses/{id} | Xóa mềm địa chỉ | Employee |

---

## 📋 MODULE: CONTRACT (25 APIs)

### Contract Controller (9 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 58 | POST /api/v1/contract | Tạo hợp đồng mới | Contract |
| 59 | PUT /api/v1/contract/{id} | Cập nhật hợp đồng | Contract |
| 60 | GET /api/v1/contract | Lấy danh sách hợp đồng active | Contract |
| 61 | GET /api/v1/contract/{id} | Lấy chi tiết hợp đồng | Contract |
| 62 | GET /api/v1/contract/not-active | Lấy hợp đồng không active | Contract |
| 63 | POST /api/v1/contract/{contractId}/approve | Duyệt/đổi trạng thái hợp đồng | Contract |
| 64 | POST /api/v1/contract/import | Import hợp đồng từ Excel | Contract |
| 65 | POST /api/v1/contract/upload-files | Upload file hợp đồng | Contract |
| 66 | GET /api/v1/contract/export | Export hợp đồng ra Excel | Contract |

### Salary Contract Controller (4 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 67 | POST /api/v1/salary-contract | Tạo hợp đồng lương | Contract |
| 68 | GET /api/v1/salary-contract/employee/{employeeId} | Lấy hợp đồng lương theo nhân viên | Contract |
| 69 | PUT /api/v1/salary-contract/{id} | Cập nhật hợp đồng lương | Contract |
| 70 | DELETE /api/v1/salary-contract/{id} | Xóa hợp đồng lương | Contract |

### Salary Adjustment Controller (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 71 | POST /api/v1/salary-adjustments | Tạo điều chỉnh lương | Contract |
| 72 | PUT /api/v1/salary-adjustments/{id} | Cập nhật điều chỉnh lương | Contract |
| 73 | DELETE /api/v1/salary-adjustments/{id} | Xóa điều chỉnh lương | Contract |
| 74 | GET /api/v1/salary-adjustments/employee/{employeeId} | Lấy điều chỉnh theo nhân viên | Contract |
| 75 | GET /api/v1/salary-adjustments/employee/{employeeId}/range | Lấy điều chỉnh trong khoảng thờii gian | Contract |

### Allowance Controller (7 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 76 | POST /api/v1/allowances | Tạo phụ cấp mới | Contract |
| 77 | GET /api/v1/allowances | Lấy danh sách phụ cấp | Contract |
| 78 | GET /api/v1/allowances/{id} | Lấy chi tiết phụ cấp | Contract |
| 79 | PUT /api/v1/allowances/{id} | Cập nhật phụ cấp | Contract |
| 80 | DELETE /api/v1/allowances/{id} | Xóa phụ cấp | Contract |
| 81 | POST /api/v1/allowances/import | Import phụ cấp từ Excel | Contract |
| 82 | GET /api/v1/allowances/export | Export phụ cấp ra Excel | Contract |

### Allowance Rule Controller (1 API)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 83 | POST /api/v1/allowance-rules | Tạo quy tắc phụ cấp | Contract |

---

## 🏢 MODULE: ORGANIZATION (19 APIs)

### Department Controller (7 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 84 | POST /api/v1/departments | Tạo phòng ban | Organization |
| 85 | GET /api/v1/departments | Lấy danh sách phòng ban | Organization |
| 86 | GET /api/v1/departments/{id} | Lấy chi tiết phòng ban | Organization |
| 87 | PUT /api/v1/departments/{id} | Cập nhật phòng ban | Organization |
| 88 | DELETE /api/v1/departments/{id} | Xóa phòng ban | Organization |
| 89 | POST /api/v1/departments/import | Import phòng ban từ Excel | Organization |
| 90 | GET /api/v1/departments/export | Export phòng ban ra Excel | Organization |

### Sub-Department Controller (8 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 91 | POST /api/v1/sub-departments | Tạo bộ phận | Organization |
| 92 | GET /api/v1/sub-departments | Lấy danh sách bộ phận | Organization |
| 93 | GET /api/v1/sub-departments/department/{departmentId} | Lấy bộ phận theo phòng ban | Organization |
| 94 | GET /api/v1/sub-departments/{id} | Lấy chi tiết bộ phận | Organization |
| 95 | PUT /api/v1/sub-departments/{id} | Cập nhật bộ phận | Organization |
| 96 | DELETE /api/v1/sub-departments/{id} | Xóa bộ phận | Organization |
| 97 | POST /api/v1/sub-departments/import | Import bộ phận từ Excel | Organization |
| 98 | GET /api/v1/sub-departments/export | Export bộ phận ra Excel | Organization |

### Position Controller (6 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 99 | POST /api/v1/positions | Tạo chức vụ | Organization |
| 100 | PUT /api/v1/positions/{id} | Cập nhật chức vụ | Organization |
| 101 | GET /api/v1/positions | Lấy danh sách chức vụ | Organization |
| 102 | GET /api/v1/positions/{id} | Lấy chi tiết chức vụ | Organization |
| 103 | POST /api/v1/positions/import | Import chức vụ từ Excel | Organization |
| 104 | GET /api/v1/positions/export | Export chức vụ ra Excel | Organization |

---

## 📅 MODULE: ATTENDANCE (16 APIs)

### Attendance Controller (3 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 105 | GET /api/v1/attendance | Lấy danh sách chấm công | Attendance |
| 106 | GET /api/v1/attendance/{id} | Lấy chi tiết chấm công | Attendance |
| 107 | GET /api/v1/attendance/sub-department/{subDepartmentId} | Lấy chấm công theo bộ phận | Attendance |

### Attendance Scan Controller (1 API)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 108 | POST /api/v1/attendance/scan | Quét khuôn mặt check-in/out | Attendance |

### Break Time Controller (5 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 109 | POST /api/v1/breaks/batch | Tạo giờ nghỉ hàng loạt | Attendance |
| 110 | POST /api/v1/breaks | Tạo giờ nghỉ | Attendance |
| 111 | PUT /api/v1/breaks/{id} | Cập nhật giờ nghỉ | Attendance |
| 112 | DELETE /api/v1/breaks/{id} | Xóa giờ nghỉ | Attendance |
| 113 | GET /api/v1/breaks/attendance/{attendanceId} | Lấy giờ nghỉ theo chấm công | Attendance |

### OT Rate Controller (6 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 114 | POST /api/v1/ot-rates | Tạo tỷ lệ OT | Attendance |
| 115 | PUT /api/v1/ot-rates/{id} | Cập nhật tỷ lệ OT | Attendance |
| 116 | DELETE /api/v1/ot-rates/{id} | Xóa tỷ lệ OT | Attendance |
| 117 | GET /api/v1/ot-rates/{id} | Lấy chi tiết tỷ lệ OT | Attendance |
| 118 | GET /api/v1/ot-rates | Lấy danh sách tỷ lệ OT | Attendance |
| 119 | GET /api/v1/ot-rates/search | Tìm tỷ lệ OT theo ngày và loại | Attendance |

---

## 🏖️ MODULE: LEAVE (9 APIs)

### Leave Balance Controller (1 API)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 120 | GET /api/leave-balance/me | Lấy số ngày phép còn lại của tôi | Leave |

### Leave Request Controller (8 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 121 | POST /api/v1/leave-requests | Tạo yêu cầu nghỉ phép | Leave |
| 122 | PUT /api/v1/leave-requests/{leaveId} | Cập nhật yêu cầu nghỉ phép | Leave |
| 123 | POST /api/v1/leave-requests/{leaveId}/approve | Duyệt/từ chối yêu cầu nghỉ phép | Leave |
| 124 | GET /api/v1/leave-requests/{leaveId} | Lấy chi tiết yêu cầu nghỉ phép | Leave |
| 125 | GET /api/v1/leave-requests | Lấy danh sách yêu cầu nghỉ phép | Leave |
| 126 | GET /api/v1/leave-requests/list | Lấy yêu cầu nghỉ phép theo trạng thái | Leave |
| 127 | GET /api/v1/leave-requests/export | Export yêu cầu nghỉ phép ra Excel | Leave |
| 128 | POST /api/v1/leave-requests/import | Import yêu cầu nghỉ phép từ Excel | Leave |

---

## 💰 MODULE: PAYROLL (13 APIs)

### Payroll Controller (10 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 129 | POST /api/v1/payroll/create | Tạo bảng lương cho 1 nhân viên | Payroll |
| 130 | POST /api/v1/payroll | Tạo bảng lương cho tất cả nhân viên | Payroll |
| 131 | GET /api/v1/payroll | Lấy danh sách bảng lương | Payroll |
| 132 | GET /api/v1/payroll/{employeeId} | Lấy bảng lương theo nhân viên và tháng | Payroll |
| 133 | POST /api/v1/payroll/approval | Duyệt/từ chối bảng lương hàng loạt | Payroll |
| 134 | GET /api/v1/payroll/list | Lấy bảng lương theo trạng thái | Payroll |
| 135 | GET /api/v1/payroll/employee/{employeeId} | Lấy lịch sử lương theo nhân viên | Payroll |
| 136 | GET /api/v1/payroll/month | Lấy bảng lương theo tháng | Payroll |
| 137 | GET /api/v1/payroll/detail/{payrollId} | Lấy chi tiết bảng lương | Payroll |

### Payroll Cycle Controller (3 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 138 | POST /api/v1/payroll-cycles | Tạo chu kỳ lương mới | Payroll |
| 139 | GET /api/v1/payroll-cycles/active | Lấy chu kỳ lương đang active | Payroll |
| 140 | GET /api/v1/payroll-cycles/history | Lấy lịch sử chu kỳ lương | Payroll |

---

## ⚠️ MODULE: PENALTY (6 APIs)

### Penalty Rule Controller (6 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 141 | POST /api/v1/penalty-rules | Tạo quy tắc phạt | Penalty |
| 142 | PUT /api/v1/penalty-rules/{id} | Cập nhật quy tắc phạt | Penalty |
| 143 | DELETE /api/v1/penalty-rules/{id} | Xóa quy tắc phạt | Penalty |
| 144 | GET /api/v1/penalty-rules/{id} | Lấy chi tiết quy tắc phạt | Penalty |
| 145 | GET /api/v1/penalty-rules | Lấy danh sách quy tắc phạt | Penalty |
| 146 | GET /api/v1/penalty-rules/active/list | Lấy quy tắc phạt đang active | Penalty |

---

## 📁 MODULE: FILE (3 APIs)

### File Attachment Controller (3 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 147 | POST /api/v1/files/upload | Upload file đính kèm | File |
| 148 | GET /api/v1/files/download/{id} | Tải xuống file | File |
| 149 | DELETE /api/v1/files/{id} | Xóa file | File |

---

## 🧪 MODULE: DEV/TEST (2 APIs)

### Dev Controllers (2 APIs)

| STT | Endpoint | Chức năng | Module |
|-----|----------|-----------|--------|
| 150 | GET /hello | Test API xin chào | Dev |
| 151 | GET /api/v1/dev/mail/test | Test gửi email (dev only) | Dev |

---

## 📊 TỔNG HỢP THEO MODULE

| Module | Số API | Tỷ lệ |
|--------|--------|-------|
| Employee | 30 | 19.9% |
| Contract | 25 | 16.6% |
| Organization | 19 | 12.6% |
| Attendance | 16 | 10.6% |
| Payroll | 13 | 8.6% |
| User | 11 | 7.3% |
| Leave | 9 | 6.0% |
| Penalty | 6 | 4.0% |
| Auth | 5 | 3.3% |
| File | 3 | 2.0% |
| Dev | 2 | 1.3% |
| **TỔNG** | **151** | **100%** |

---

*Ghi chú: `${app.api-prefix}` mặc định là `/api/v1` theo cấu hình application.yml*
