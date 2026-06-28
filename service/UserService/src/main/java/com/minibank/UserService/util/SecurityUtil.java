package com.minibank.UserService.util;

import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {
    public static Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
