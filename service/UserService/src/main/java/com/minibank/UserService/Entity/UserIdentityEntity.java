package com.minibank.UserService.Entity;

import java.time.Instant;
import java.time.LocalDate;

import com.minibank.UserService.Enum.KycStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="user_identities")
@Data
public class UserIdentityEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;
    @Column(nullable=false)
    private String fullName;
    @Column(nullable=false, unique=true)
    private String idCardNumber;
    @Column(nullable=false)
    private LocalDate issueDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus kycStatus;
    @Column(nullable=false)
    private String idCardFrontUrl;
    @Column(nullable=false)
    private String idCardBackUrl;
    @Column(nullable=false)
    private LocalDate dateOfBirth;
    private Instant submittedAt;
    private Instant verifiedAt;
}
