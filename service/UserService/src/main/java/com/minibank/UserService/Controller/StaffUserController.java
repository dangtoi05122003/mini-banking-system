package com.minibank.UserService.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.UserService.Enum.StaffRole;
import com.minibank.UserService.Service.StaffUserService;
import com.minibank.UserService.dto.Request.StaffUser.StaffUserRequest;
import com.minibank.UserService.dto.Request.StaffUser.UpdateStaffRequest;
import com.minibank.UserService.dto.Request.User.ChangePasswordRequest;
import com.minibank.UserService.dto.Response.StaffUserResponse;

@RestController
@RequestMapping("/staff")
public class StaffUserController {
    @Autowired
    private StaffUserService staffUserService;
    @PostMapping("/register")
    public StaffUserResponse createStaff(@RequestBody StaffUserRequest request) {
        return staffUserService.createStaff(request);
    }
    @PostMapping("/reset-password")
    public StaffUserResponse sendResetPasswordOtp(@RequestParam String email) {
        return staffUserService.sendResetPasswordOtp(email);
    }
    @PostMapping("/verify-password")
    public StaffUserResponse verifyOtp(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        return staffUserService.verifyPassword(email, otp, newPassword);
    }
    @GetMapping("/{id}")
    public StaffUserResponse getStaffById(@PathVariable Long id) {
        return staffUserService.getStaffById(id);
    }
    @PutMapping("/{id}/lock")
    public StaffUserResponse lockStaff(@PathVariable Long id) {
        return staffUserService.lockStaff(id);
    }
    @PutMapping("/{id}/unlock")
    public StaffUserResponse unlockStaff(@PathVariable Long id) {
        return staffUserService.unlockStaff(id);
    }
    @PutMapping("/change-password")
    public StaffUserResponse changePassword(@RequestBody ChangePasswordRequest request) {
        return staffUserService.changePassword(request);
    }
    @PutMapping("/{id}/role/{role}")
    public StaffUserResponse changeRole(@PathVariable Long id, @PathVariable StaffRole role) {
        return staffUserService.changeRole(id, role);
    }
    @GetMapping
    public List<StaffUserResponse> getAllStaff() {
        return staffUserService.getAllStaff();
    }
    @PutMapping
    public StaffUserResponse updateStaff(@RequestBody UpdateStaffRequest request) {
        return staffUserService.updateStaff(request);
    }
    @DeleteMapping("/{id}")
    public StaffUserResponse deleteStaff(@PathVariable Long id) {
        return staffUserService.deleteStaff(id);
    }
    @GetMapping("/me")
    public StaffUserResponse getMyInfo() {
        return staffUserService.getMyInfo();
    }
}
