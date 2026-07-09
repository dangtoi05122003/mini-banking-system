package com.minibank.AccountService.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.minibank.AccountService.Entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long>{
    boolean existsByAccountNumber(String accountNumber);
    @Query("SELECT a FROM AccountEntity a WHERE a.user_id = :userId")
    List<AccountEntity> findByUserId(@Param("userId") Long userId);
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
