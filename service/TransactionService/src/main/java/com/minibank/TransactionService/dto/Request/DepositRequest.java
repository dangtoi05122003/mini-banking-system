package com.minibank.TransactionService.dto.Request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DepositRequest {
    @NotNull(message = "Thông tin tài khoản không được để trống")
    private Long accountId;
    @NotNull(message = "Số tiền nạp không được để trống")
    @DecimalMin(value = "1000.0", message = "Số tiền nạp tối thiểu là 1000")
    private BigDecimal amount;
    @NotBlank(message = "Mô tả giao dịch không được để trống")
    private String description;
}
