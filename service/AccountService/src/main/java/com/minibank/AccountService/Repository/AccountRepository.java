package com.minibank.AccountService.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.minibank.AccountService.Entity.AccountEntity;
import com.minibank.AccountService.Enum.AccountStatus;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<AccountEntity, Long>{
    boolean existsByAccountNumber(String accountNumber);
    @Query("SELECT a FROM AccountEntity a WHERE a.user_id = :userId")
    List<AccountEntity> findAllByUserId(@Param("userId") Long userId);
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountEntity a WHERE a.accountNumber = :accountNumber")
    Optional<AccountEntity> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);
    @Modifying
    @Query("UPDATE AccountEntity a SET a.status = :newStatus WHERE a.user_id = :userId AND a.status = :oldStatus")
    int updateAccountStatusByUserId(@Param("userId") Long userId,  @Param("oldStatus") AccountStatus oldStatus,  @Param("newStatus") AccountStatus newStatus);
    @Query("SELECT COUNT(a) > 0 FROM AccountEntity a WHERE a.user_id = :user_id")
    boolean existsByUserId(@Param("user_id") Long user_id);
    @Query("SELECT a FROM AccountEntity a WHERE a.accountNumber = :accountNumber AND a.user_id = :user_id")
    Optional<AccountEntity> findByAccountNumberAndUserId(@Param("accountNumber") String accountNumber, @Param("user_id") Long user_id);
    @Query(" SELECT a FROM AccountEntity a WHERE a.user_id = :user_id AND a.isPrimary = true")
    Optional<AccountEntity> findByUserIdAndIsPrimaryTrue(@Param("user_id") Long user_id);
}
