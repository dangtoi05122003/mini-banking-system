package com.minibank.TransactionService.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minibank.TransactionService.Entity.TransactionEntity;
import com.minibank.TransactionService.Enum.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>{
    @Query("SELECT t FROM TransactionEntity t WHERE t.senderAccountNumber = :accountNumber OR t.receiverAccountNumber = :accountNumber ORDER BY t.createdAt DESC")
    Page<TransactionEntity> findByAccountNumber(@Param("accountNumber") String accountNumber, Pageable pageable);
    Optional<TransactionEntity> findByTransactionCode(String transactionCode);
    Page<TransactionEntity> findByType(TransactionType type, Pageable pageable);
    @Query("SELECT t FROM TransactionEntity t WHERE (t.senderAccountNumber = :accountNumber OR t.receiverAccountNumber = :accountNumber) AND t.type = :type ORDER BY t.createdAt DESC")
    Page<TransactionEntity> findByAccountNumberAndType(@Param("accountNumber") String accountNumber, @Param("type") TransactionType type, Pageable pageable);
}
