package com.minibank.UserService.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minibank.UserService.Enum.StaffStatus;
import com.minibank.UserService.Enum.UserStatus;
import com.minibank.UserService.Exception.AppException;
import com.minibank.UserService.Exception.ErrorCode;
import com.minibank.UserService.Repository.StaffUserRepository;
import com.minibank.UserService.Repository.UserRepository;
import com.minibank.UserService.dto.Request.Auth.AuthRequest;
import com.minibank.UserService.dto.Request.Auth.RefreshRequest;
import com.minibank.UserService.dto.Response.AuthResponse;
import com.minibank.UserService.dto.Response.RefreshResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
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
        String accessToken = generateToken(
            user.getId(), user.getUsername(), "CUSTOMER", "USER", "ACCESS",15, ChronoUnit.MINUTES
        );
        String refreshToken = generateToken(
            user.getId(), user.getUsername(), "CUSTOMER", "USER", "REFRESH",7, ChronoUnit.DAYS
        );
        String key = "refresh:USER:" + user.getId();
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
        return new AuthResponse(accessToken, refreshToken);
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
        String accessToken = generateToken(
            user.getId(), user.getEmail(), user.getRole().name(),"STAFF", "ACCESS",15, ChronoUnit.MINUTES
        );
        String refreshToken = generateToken(
            user.getId(), user.getEmail(), user.getRole().name(), "STAFF", "REFRESH", 7, ChronoUnit.DAYS
        );
        String key = "refresh:STAFF:" + user.getId();
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
        return new AuthResponse(accessToken, refreshToken);
    }
    public RefreshResponse refreshToken(RefreshRequest request) {
        try {
            var signedJWT = verifyJwt(request.getToken());
            String type = signedJWT.getJWTClaimsSet().getStringClaim("type");
            String accountType = signedJWT.getJWTClaimsSet().getStringClaim("accountType");
            if (!"REFRESH".equals(type)) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
            if (!"USER".equals(accountType) && !"STAFF".equals(accountType)) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
            Long id = Long.valueOf(signedJWT.getJWTClaimsSet().getSubject());
            String key = "refresh:" + accountType + ":" + id;
            String storedToken = redisTemplate.opsForValue().get(key);
            if (storedToken == null || !storedToken.equals(request.getToken())) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
            String username = signedJWT.getJWTClaimsSet().getStringClaim("username");
            String role = signedJWT.getJWTClaimsSet().getStringClaim("role");
            String accessToken = generateToken(id, username, role, accountType, "ACCESS",15, ChronoUnit.MINUTES);
            return new RefreshResponse(accessToken);
        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }
    private SignedJWT verifyJwt(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        return signedJWT;
    }
    public String generateToken(Long id, String username, String role, String accountType, String tokenType,long expirationTime, ChronoUnit unit) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(id.toString())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(Date.from(Instant.now().plus(expirationTime, unit)))
                .claim("username", username)
                .claim("type", tokenType)
                .claim("accountType", accountType);
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
    public void logout(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
            Long id = Long.valueOf(signedJWT.getJWTClaimsSet().getSubject());
            String accountType = signedJWT.getJWTClaimsSet().getStringClaim("accountType");
            redisTemplate.delete("refresh:" + accountType + ":" + id);
        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }
}
