package com.minibank.UserService.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minibank.UserService.Enum.KycStatus;
import com.minibank.UserService.Service.UserIdentityService;
import com.minibank.UserService.dto.Request.UserIdentity.UserIdentityRequest;
import com.minibank.UserService.dto.Response.UserIdentityResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user-identity")
public class UserIdentityController {
    @Autowired
    private UserIdentityService userIdentityService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserIdentityResponse createUserIdentity( @Valid @ModelAttribute UserIdentityRequest request) {
        return userIdentityService.createUserIdentity(request);
    }
    @PatchMapping("/{id}/status")
    public UserIdentityResponse updateStatus(@PathVariable Long id, @RequestParam KycStatus status) {
        return userIdentityService.updateStatus(id, status);
    }
    @GetMapping("/me")
    public UserIdentityResponse getMyIdentity() {
        return userIdentityService.getMyIdentity();
    }
    @GetMapping("/{id}")
    public UserIdentityResponse getById(@PathVariable Long id) {
        return userIdentityService.getById(id);
    }
    @GetMapping("/pending")
    public List<UserIdentityResponse> getPendingIdentities() {
        return userIdentityService.getPendingIdentities();
    }
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserIdentityResponse updateIdentity(@Valid @ModelAttribute UserIdentityRequest request) {
        return userIdentityService.updateIdentity(request);
    }
}