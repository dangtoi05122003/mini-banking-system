package com.minibank.AccountService.dto.Response;

import java.math.BigDecimal;

import com.minibank.AccountService.Entity.AccountEntity;
import com.minibank.AccountService.Enum.AccountStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountResponse {
    private Long id;
    private Long user_id;
    private AccountStatus status;
    private String accountNumber;
    private BigDecimal balance;
    private Boolean isPrimary;
    public static AccountResponse toResponse(AccountEntity account) {
        return AccountResponse.builder()
            .id(account.getId())
            .user_id(account.getUser_id())
            .status(account.getStatus())
            .accountNumber(account.getAccountNumber())
            .balance(account.getBalance())
            .isPrimary(account.getIsPrimary())
            .build();
    }
}
