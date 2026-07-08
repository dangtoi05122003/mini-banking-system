package com.minibank.AccountService.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.minibank.AccountService.Entity.AccountEntity;
import com.minibank.AccountService.Enum.AccountStatus;
import com.minibank.AccountService.Exception.AppException;
import com.minibank.AccountService.Exception.ErrorCode;
import com.minibank.AccountService.Repository.AccountRepository;
import com.minibank.AccountService.dto.Response.AccountResponse;
import static com.minibank.AccountService.util.SecurityUtil.getCurrentUserId;

@Service
public class AccountService {
    private final SecureRandom random = new SecureRandom();
    @Autowired
    private AccountRepository accountRepository;
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public AccountResponse createAccount () {
        Long user_id = getCurrentUserId();
        AccountEntity account = new AccountEntity();
        account.setStatus(AccountStatus.ACTIVE);
        account.setUser_id(user_id);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    private String generateAccountNumber() {
        for (int i = 0; i < 55; i++) {
            long number = Math.abs(random.nextLong() % 900000000000L) + 100000000000L;
            String accountNumber = String.valueOf(number);
            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }
        throw new AppException(ErrorCode.ACCOUNT_NUMBER_GENERATION_FAILED);
    }
}