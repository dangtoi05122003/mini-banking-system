package com.minibank.AccountService.Entity;

import java.math.BigDecimal;

import com.minibank.AccountService.Enum.AccountStatus;

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
@Table(name="account")
@Data
public class AccountEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long user_id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;
    @Column(nullable = false, unique = true)
    private String accountNumber;
    private BigDecimal balance;
    private Boolean isPrimary;
}
