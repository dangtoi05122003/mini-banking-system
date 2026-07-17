package com.minibank.UserService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.UserService.Service.AuthService;
import com.minibank.UserService.dto.Request.Auth.AuthRequest;
import com.minibank.UserService.dto.Request.Auth.RefreshRequest;
import com.minibank.UserService.dto.Response.AuthResponse;
import com.minibank.UserService.dto.Response.RefreshResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/customer")
    public AuthResponse loginCustomer(@Valid @RequestBody AuthRequest request) {
        return authService.loginCustomer(request);
    }
    @PostMapping("/staff")
    public AuthResponse loginStaff(@Valid @RequestBody AuthRequest request) {
        return authService.loginStaff(request);
    }
    @PostMapping("/refresh")
    public RefreshResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }
    @PostMapping("/logout")
    public void logout(@RequestParam String accessToken) {
        authService.logout(accessToken);
    }
}
