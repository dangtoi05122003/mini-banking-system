package com.minibank.UserService.dto.Response;

import com.minibank.UserService.Enum.StaffRole;
import com.minibank.UserService.Enum.StaffStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffUserResponse {
    private Long id;
    private String username;
    private String email;
    private StaffRole role;
    private StaffStatus status;
}
