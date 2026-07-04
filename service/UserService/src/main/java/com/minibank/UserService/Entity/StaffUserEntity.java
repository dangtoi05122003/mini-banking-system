package com.minibank.UserService.Entity;

import java.time.Instant;

import com.minibank.UserService.Enum.StaffRole;
import com.minibank.UserService.Enum.StaffStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "staff_users")
public class StaffUserEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffStatus status;
    @Column(nullable = false)
    private int authenticationFailureCount = 0;
    private Instant lockedAt;
}
