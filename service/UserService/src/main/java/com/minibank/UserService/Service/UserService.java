package com.minibank.UserService.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minibank.UserService.Entity.UserEntity;
import com.minibank.UserService.Enum.EmailType;
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
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final SecureRandom random = new SecureRandom();
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
        user.setStatus(UserStatus.PENDING);
        String otp = generateOtp();
        String key = "otp:" + request.getEmail();
        redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);
        emailService.sendOtpEmail(request.getEmail(), otp, EmailType.VERIFY_ACCOUNT);
        return UserMapper.toResponse(userRepository.save(user));
    }
    public UserResponse sendResetPasswordOtp(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String otp = generateOtp();
        String key = "otp:reset:" + email;
        redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);
        emailService.sendOtpEmail(email, otp, EmailType.RESET_PASSWORD);
        return UserMapper.toResponse(userRepository.save(user));
    }
    public String generateOtp() {
        return String.valueOf(100000 + random.nextInt(900000));
    }
    public UserResponse verifyUser(String email, String otp) {
        String key = "otp:" + email;
        String cachedOtp = redisTemplate.opsForValue().get(key);
        if (cachedOtp == null) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        if (!cachedOtp.equals(otp)) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        redisTemplate.delete(key);
        return UserMapper.toResponse(user);
    }
    public UserResponse verifyPassword(String email, String otp, String newPassword) {
        String key = "otp:reset:" + email;
        String cachedOtp = redisTemplate.opsForValue().get(key);
        if (cachedOtp == null) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        if (!cachedOtp.equals(otp)) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisTemplate.delete(key);
        return UserMapper.toResponse(user);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        return UserMapper.toResponse(user);
    }
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public UserResponse getMe() {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        return UserMapper.toResponse(user);
    }
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public UserResponse updateProfile(UserRequest request) {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        return UserMapper.toResponse(userRepository.save(user));
    }
    @PreAuthorize("hasAnyRole('CUSTOMER')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse unlockUser(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(UserStatus.ACTIVE);
        user.setAuthenticationFailureCount(0);
        user.setLockedAt(null);
        return UserMapper.toResponse(userRepository.save(user));
    }
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public UserResponse deleteMyAccount() {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        validateUserStatus(user);
        user.setStatus(UserStatus.DELETED);
        return UserMapper.toResponse(userRepository.save(user));
    }
    @PreAuthorize("hasRole('ADMIN')")
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
