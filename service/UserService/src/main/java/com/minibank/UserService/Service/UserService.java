package com.minibank.UserService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minibank.UserService.Entity.UserEntity;
import com.minibank.UserService.Enum.UserStatus;
import com.minibank.UserService.Exception.AppException;
import com.minibank.UserService.Exception.ErrorCode;
import com.minibank.UserService.Mapper.UserMapper;
import com.minibank.UserService.Repository.UserRepository;
import com.minibank.UserService.dto.Request.User.ChangePasswordRequest;
import com.minibank.UserService.dto.Request.User.UserRequest;
import com.minibank.UserService.dto.Response.UserResponse;
import static com.minibank.UserService.util.SecurityUtil.getCurrentUserId;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        return UserMapper.toResponse(userRepository.save(user));
    }
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        return UserMapper.toResponse(user);
    }
    public UserResponse getMe() {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        return UserMapper.toResponse(user);
    }
    public UserResponse updateProfile(UserRequest request) {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        return UserMapper.toResponse(userRepository.save(user));
    }
    public UserResponse changePassword(ChangePasswordRequest request) {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return UserMapper.toResponse(userRepository.save(user));
    }
    public UserResponse unlockUser(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(UserStatus.ACTIVE);
        user.setAuthenticationFailureCount(0);
        user.setLockedAt(null);
        return UserMapper.toResponse(userRepository.save(user));
    }
    public UserResponse deleteMyAccount() {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        user.setStatus(UserStatus.DELETED);
        return UserMapper.toResponse(userRepository.save(user));
    }
    public UserResponse suspendUser(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() == UserStatus.DELETED) {
            throw new AppException(ErrorCode.USER_DELETED);
        }
        user.setStatus(UserStatus.SUSPENDED);
        return UserMapper.toResponse(userRepository.save(user));
    }
    private void validateUserStatus(UserEntity user) {
        if (user.getStatus() == UserStatus.DELETED) {
            throw new AppException(ErrorCode.USER_DELETED);
        }
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new AppException(ErrorCode.USER_LOCKED);
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new AppException(ErrorCode.USER_SUSPENDED);
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        }
    }
}
