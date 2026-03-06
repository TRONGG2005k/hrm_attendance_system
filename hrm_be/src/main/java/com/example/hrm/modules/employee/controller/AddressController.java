package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.AddressRequest;
import com.example.hrm.modules.employee.dto.response.AddressResponse;
import com.example.hrm.modules.employee.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Lấy danh sách tất cả địa chỉ (có phân trang)
     *
     * @param page Trang (mặc định 0)
     * @param size Số lượng bản ghi trên một trang (mặc định 10)
     * @return Danh sách địa chỉ
     */
    @GetMapping
    public ResponseEntity<Page<AddressResponse>> getAllAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AddressResponse> addresses = addressService.getAllAddresses(page, size);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Lấy thông tin chi tiết địa chỉ theo ID
     *
     * @param id ID của địa chỉ
     * @return Thông tin địa chỉ
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable String id) {
        AddressResponse address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    /**
     * Tạo địa chỉ mới
     *
     * @param request Dữ liệu địa chỉ từ request
     * @return Địa chỉ vừa tạo
     */
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@RequestBody @Valid AddressRequest request) {
        AddressResponse address = addressService.createAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    /**
     * Cập nhật thông tin địa chỉ
     *
     * @param id ID của địa chỉ cần cập nhật
     * @param request Dữ liệu cập nhật
     * @return Địa chỉ sau khi cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable String id,
            @RequestBody @Valid AddressRequest request) {
        AddressResponse address = addressService.updateAddress(id, request);
        return ResponseEntity.ok(address);
    }

    /**
     * Xóa mềm địa chỉ (đánh dấu là đã xóa)
     *
     * @param id ID của địa chỉ cần xóa
     * @return Trạng thái xóa thành công
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
