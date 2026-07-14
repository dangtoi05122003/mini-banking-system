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
    @Query("SELECT t FROM TransactionEntity t WHERE t.senderAccountNumber = :accountNumber OR t.receiverAccountNumber = :accountNumber")
    List<TransactionEntity> findByAccountNumber(@Param("accountNumber") String accountNumber);
    Optional<TransactionEntity> findByTransactionCode(String transactionCode);
    List<TransactionEntity> findByType(TransactionType type);
    @Query("SELECT t FROM TransactionEntity t WHERE (t.senderAccountNumber = :accountNumber OR t.receiverAccountNumber = :accountNumber) AND t.type = :type")
    List<TransactionEntity> findByAccountNumberAndType(@Param("accountNumber") String accountNumber, @Param("type") TransactionType type);
}
