package com.minibank.AccountService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minibank.AccountService.Entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long>{
    boolean existsByAccountNumber(String accountNumber);
}
