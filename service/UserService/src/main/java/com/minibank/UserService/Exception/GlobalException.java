package com.minibank.UserService.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.minibank.UserService.dto.Response.ApiResponse;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse(errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ApiResponse apiResponse = new ApiResponse(400, exception.getBindingResult().getFieldError().getDefaultMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
