package com.minibank.TransactionService.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minibank.TransactionService.Entity.TransactionEntity;
import com.minibank.TransactionService.Enum.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>{
    @Query("SELECT t FROM TransactionEntity t WHERE t.senderAccountId = :accountId OR t.receiverAccountId = :accountId")
    List<TransactionEntity> findByAccountId(@Param("accountId") Long accountIds);
    Optional<TransactionEntity> findByTransactionCode(String transactionCode);
    List<TransactionEntity> findByType(TransactionType type);
    @Query("SELECT t FROM TransactionEntity t WHERE (t.senderAccountId = :accountId OR t.receiverAccountId = :accountId) AND t.type = :type")
    List<TransactionEntity> findByAccountIdAndType(@Param("accountId") Long accountId, @Param("type") TransactionType type);
}
