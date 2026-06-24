package com.minibank.UserService.dto.Request.User;

import com.minibank.UserService.Enum.UserStatus;

import lombok.Getter;

@Getter
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private UserStatus status;
}
