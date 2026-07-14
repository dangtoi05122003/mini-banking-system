package com.minibank.AccountService.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.minibank.AccountService.Entity.AccountEntity;
import com.minibank.AccountService.Enum.AccountStatus;
import com.minibank.AccountService.Enum.KycStatus;
import com.minibank.AccountService.Exception.AppException;
import com.minibank.AccountService.Exception.ErrorCode;
import com.minibank.AccountService.Repository.AccountRepository;
import com.minibank.AccountService.dto.Request.TransferRequest;
import com.minibank.AccountService.dto.Response.AccountResponse;
import static com.minibank.AccountService.util.SecurityUtil.getCurrentUserId;

import jakarta.transaction.Transactional;

@Service
public class AccountService {
    private final SecureRandom random = new SecureRandom();
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserServiceClient userServiceClient;
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public AccountResponse createAccount () {
        Long user_id = getCurrentUserId();
        AccountStatus status = AccountStatus.PENDING;
        try {
            KycStatus kycStatus = userServiceClient.getKycStatusByUserId(user_id);
            if (kycStatus == KycStatus.APPROVED) {
                status = AccountStatus.ACTIVE;
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_SERVICE_UNAVAILABLE);
        }
        AccountEntity account = new AccountEntity();
        account.setStatus(status);
        account.setUser_id(user_id);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public List<AccountResponse> getMyAccount() {
        Long user_id = getCurrentUserId();
        List<AccountEntity> accounts = accountRepository.findAllByUserId(user_id);
        if (accounts.isEmpty()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return accounts.stream()
            .map(AccountResponse::toResponse)
            .toList();
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public AccountResponse getAccountByNumber(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return AccountResponse.toResponse(account);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    public BigDecimal getBalance(String accountNumber) {
        Long userId = getCurrentUserId();
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (!account.getUser_id().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        checkStatus(account);
        return account.getBalance();
    }
    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public AccountResponse transfer(TransferRequest request) {
        Long userId = getCurrentUserId();
        AccountEntity sender = accountRepository.findByAccountNumberForUpdate( request.getSenderAccountNumber()).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (!sender.getUser_id().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        AccountEntity receiver = accountRepository.findByAccountNumberForUpdate(request.getReceiverAccountNumber()).orElseThrow(() ->new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if(sender.getAccountNumber().equals(receiver.getAccountNumber())) {
            throw new AppException(ErrorCode.SELF_TRANSFER_NOT_ALLOWED);
        }
        checkStatus(sender);
        checkStatus(receiver);
        checkAmount(sender, request.getAmount());
        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));
        accountRepository.save(sender);
        accountRepository.save(receiver);
        return AccountResponse.toResponse(sender);
    }
    @Transactional
    public AccountResponse transferService(TransferRequest request) {
        AccountEntity sender = accountRepository.findByAccountNumber( request.getSenderAccountNumber()).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        AccountEntity receiver = accountRepository.findByAccountNumber(request.getReceiverAccountNumber()).orElseThrow(() ->new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if(sender.getAccountNumber().equals(receiver.getAccountNumber())) {
            throw new AppException(ErrorCode.SELF_TRANSFER_NOT_ALLOWED);
        }
        checkStatus(sender);
        checkStatus(receiver);
        checkAmount(sender, request.getAmount());
        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));
        accountRepository.save(sender);
        accountRepository.save(receiver);
        return AccountResponse.toResponse(sender);
    }
    @PreAuthorize("hasAnyRole('TELLER')")
    @Transactional
    public AccountResponse deposit(String accountNumber, BigDecimal amount) {
        AccountEntity account = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        checkStatus(account);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }
        account.setBalance(account.getBalance().add(amount));
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    public AccountResponse depositService(String accountNumber, BigDecimal amount) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        checkStatus(account);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }
        account.setBalance(account.getBalance().add(amount));
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    @PreAuthorize("hasRole('TELLER')")
    @Transactional
    public AccountResponse withdraw(String accountNumber, BigDecimal amount) {
        AccountEntity account = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        checkStatus(account);
        checkAmount(account, amount);
        account.setBalance(account.getBalance().subtract(amount));
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    public AccountResponse withdrawService(String accountNumber, BigDecimal amount) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        checkStatus(account);
        checkAmount(account, amount);
        account.setBalance(account.getBalance().subtract(amount));
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    @PreAuthorize("hasAnyRole('ADMIN','TELLER')")
    public AccountResponse freeze(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AppException(ErrorCode.ACCOUNT_FROZEN);
        }
        account.setStatus(AccountStatus.FROZEN);
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    public AccountResponse lock(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        account.setStatus(AccountStatus.LOCKED);
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    @PreAuthorize("hasAnyRole('ADMIN','TELLER')")
    public AccountResponse unlock(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumberForUpdate(accountNumber).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
        }
        account.setStatus(AccountStatus.ACTIVE);
        return AccountResponse.toResponse(accountRepository.save(account));
    }
    @Transactional
    public int activatePendingAccountsByUserId(Long userId) {
        return accountRepository.updateAccountStatusByUserId(userId, AccountStatus.PENDING, AccountStatus.ACTIVE);
    }
    private void checkStatus(AccountEntity account) {
        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new AppException(ErrorCode.ACCOUNT_FROZEN);
        }
        if (account.getStatus() == AccountStatus.PENDING) {
            throw new AppException(ErrorCode.ACCOUNT_PENDING_APPROVAL);
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
    }
    private void checkAmount(AccountEntity sender, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }
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