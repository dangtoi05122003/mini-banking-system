package com.minibank.UserService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.UserService.Service.UserService;
import com.minibank.UserService.dto.Request.User.ChangePasswordRequest;
import com.minibank.UserService.dto.Request.User.UserRequest;
import com.minibank.UserService.dto.Response.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }
    @PostMapping("/change-password")
    public UserResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request);
    }
    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getMe();
    }
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    @PostMapping("/update-profile")
    public UserResponse updateProfile(@RequestBody UserRequest request) {
        return userService.updateProfile(request);
    }
    @PostMapping("/delete-account")
    public UserResponse deleteMyAccount() {
        return userService.deleteMyAccount();
    }
    @PostMapping("/suspend/{id}")
    public UserResponse suspendUser(@PathVariable Long id) {
        return userService.suspendUser(id);
    }
    @PostMapping("/unlock/{id}")
    public UserResponse unlockUser(@PathVariable Long id) {
        return userService.unlockUser(id);
    }
}
