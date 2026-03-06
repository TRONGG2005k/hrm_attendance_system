# PowerShell Script to migrate files to modular structure
# Run this script in the project root directory

$sourceDir = "src\main\java\com\example\hrm"
$targetModulesDir = "src\main\java\com\example\hrm\modules"

# Mapping của entities tới modules
$entityMapping = @{
    "Attendance" = "attendance"
    "AttendancePenalty" = "attendance"
    "BreakTime" = "attendance"
    "OTRate" = "attendance"
    "Employee" = "employee"
    "Contact" = "employee"
    "SalaryAdjustment" = "employee"
    "Department" = "organization"
    "SubDepartment" = "organization"
    "Address" = "organization"
    "Province" = "organization"
    "District" = "organization"
    "Ward" = "organization"
    "Payroll" = "payroll"
    "PayrollCycle" = "payroll"
    "PayrollApprovalHistory" = "payroll"
    "SalaryContract" = "payroll"
    "PenaltyRule" = "penalty"
    "PenaltySource" = "penalty"
    "Contract" = "contract"
    "FileAttachment" = "file"
    "UserAccount" = "user"
    "Role" = "user"
    "Permission" = "user"
}

# Mapping của services tới modules
$serviceMapping = @{
    "AttendanceService" = "attendance"
    "AttendanceCheckInService" = "attendance"
    "AttendanceCheckOutService" = "attendance"
    "AttendancePenaltyService" = "attendance"
    "AttendanceScanService" = "attendance"
    "BreakTimeService" = "attendance"
    "OTRateService" = "attendance"
    "EmployeeService" = "employee"
    "ContactService" = "employee"
    "SalaryAdjustmentService" = "employee"
    "DepartmentService" = "organization"
    "SubDepartmentService" = "organization"
    "AddressService" = "organization"
    "ProvinceService" = "organization"
    "DistrictService" = "organization"
    "WardService" = "organization"
    "PayrollService" = "payroll"
    "PayrollCycleService" = "payroll"
    "SalaryContractService" = "payroll"
    "PenaltyRuleService" = "penalty"
    "PenaltyService" = "penalty"
    "ContractService" = "contract"
    "FileUploadService" = "file"
    "FileAttachmentService" = "file"
    "UserAccountService" = "user"
    "RoleService" = "user"
    "AuthService" = "auth"
    "JwtService" = "auth"
    "FaceRecognitionService" = "face-recognition"
    "EmailService" = "email"
}

# Mapping của controllers tới modules
$controllerMapping = @{
    "AttendanceController" = "attendance"
    "AttendanceScanController" = "attendance"
    "EmployeeController" = "employee"
    "ContactController" = "employee"
    "SalaryAdjustmentController" = "employee"
    "DepartmentController" = "organization"
    "SubDepartmentController" = "organization"
    "AddressController" = "organization"
    "ProvinceController" = "organization"
    "DistrictController" = "organization"
    "WardController" = "organization"
    "PayrollCycleController" = "payroll"
    "SalaryContractController" = "payroll"
    "PenaltyRuleController" = "penalty"
    "ContractController" = "contract"
    "FileAttachmentController" = "file"
    "UserAccountController" = "user"
    "RoleController" = "user"
    "AuthController" = "auth"
    "FaceRecognitionController" = "face-recognition"
    "EmailTestController" = "email"
}

Write-Host "Starting migration to modular structure..." -ForegroundColor Green

# Move entities
Write-Host "`nMoving entities..." -ForegroundColor Yellow
foreach ($entity in $entityMapping.GetEnumerator()) {
    $file = "$sourceDir\entity\$($entity.Key).java"
    $target = "$targetModulesDir\$($entity.Value)\entity\$($entity.Key).java"
    
    if (Test-Path $file) {
        Move-Item -Path $file -Destination $target -Force
        Write-Host "✓ Moved $($entity.Key).java to $($entity.Value)" -ForegroundColor Green
    }
}

# Move services
Write-Host "`nMoving services..." -ForegroundColor Yellow
foreach ($service in $serviceMapping.GetEnumerator()) {
    $file = "$sourceDir\service\$($service.Key).java"
    $target = "$targetModulesDir\$($service.Value)\service\$($service.Key).java"
    
    if (Test-Path $file) {
        Move-Item -Path $file -Destination $target -Force
        Write-Host "✓ Moved $($service.Key).java to $($service.Value)" -ForegroundColor Green
    }
}

# Move controllers
Write-Host "`nMoving controllers..." -ForegroundColor Yellow
foreach ($controller in $controllerMapping.GetEnumerator()) {
    $file = "$sourceDir\controller\$($controller.Key).java"
    $target = "$targetModulesDir\$($controller.Value)\controller\$($controller.Key).java"
    
    if (Test-Path $file) {
        Move-Item -Path $file -Destination $target -Force
        Write-Host "✓ Moved $($controller.Key).java to $($controller.Value)" -ForegroundColor Green
    }
}

Write-Host "`nMigration completed!" -ForegroundColor Green
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Update all imports in migrated files"
Write-Host "2. Move DTOs and Mappers to their respective modules"
Write-Host "3. Move shared configuration files to shared/ directory"
Write-Host "4. Run 'mvn clean compile' to check for errors"
