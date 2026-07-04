package com.minibank.UserService.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minibank.UserService.Enum.StaffStatus;
import com.minibank.UserService.Enum.UserStatus;
import com.minibank.UserService.Exception.AppException;
import com.minibank.UserService.Exception.ErrorCode;
import com.minibank.UserService.Repository.UserRepository;
import com.minibank.UserService.Repository.StaffUserRepository;
import com.minibank.UserService.dto.Request.Auth.AuthRequest;
import com.minibank.UserService.dto.Response.AuthResponse;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.signerkey}")
    private String SECRET_KEY;
    private static final int LOGIN_FAILURE_THRESHOLD = 5;
    public AuthResponse loginCustomer(AuthRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new AppException(ErrorCode.USER_LOCKED);
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new AppException(ErrorCode.USER_SUSPENDED);
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int failCount = user.getAuthenticationFailureCount() + 1;
            user.setAuthenticationFailureCount(failCount);
            if (failCount >= LOGIN_FAILURE_THRESHOLD) {
                user.setStatus(UserStatus.LOCKED);
                user.setLockedAt(Instant.now());
            }
            userRepository.save(user);
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
        user.setAuthenticationFailureCount(0);
        user.setLockedAt(null);
        userRepository.save(user);
        String token = generateToken(
            user.getId(), user.getUsername(), "CUSTOMER"
        );
        return new AuthResponse((token));
    }
    public AuthResponse loginStaff(AuthRequest request) {
        var user = staffUserRepository.findByEmail(request.getUsername()).orElseThrow(()-> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (user.getStatus() == StaffStatus.LOCKED) {
            throw new AppException(ErrorCode.STAFF_LOCKED);
        }
        if (user.getStatus() == StaffStatus.TERMINATED) {
            throw new AppException(ErrorCode.STAFF_TERMINATED);
        }
        if (user.getStatus() != StaffStatus.ACTIVE) {
            throw new AppException(ErrorCode.STAFF_NOT_ACTIVE);
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int failCount = user.getAuthenticationFailureCount() + 1;
            user.setAuthenticationFailureCount(failCount);
            if (failCount >= LOGIN_FAILURE_THRESHOLD) {
                user.setStatus(StaffStatus.LOCKED);
                user.setLockedAt(Instant.now());
            }
            staffUserRepository.save(user);
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
        user.setAuthenticationFailureCount(0);
        user.setLockedAt(null);
        staffUserRepository.save(user);
        String token = generateToken(
            user.getId(), user.getEmail(), user.getRole().name()
        );
        return new AuthResponse((token));
    }
    public String generateToken(Long id, String username, String role) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(id.toString())
                .expirationTime(Date.from(Instant.now().plus(12, ChronoUnit.HOURS)))
                .claim("username", username);
            if (role != null){
                builder.claim("role", role);
            }
            SignedJWT signedJWT = new SignedJWT(header, builder.build());
            signedJWT.sign(signer);
            return signedJWT.serialize();
        }catch(Exception e) {
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }
}
