package com.minibank.UserService.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minibank.UserService.Entity.UserEntity;
import com.minibank.UserService.Entity.UserIdentityEntity;
import com.minibank.UserService.Enum.KycStatus;
import com.minibank.UserService.Exception.AppException;
import com.minibank.UserService.Exception.ErrorCode;
import com.minibank.UserService.Mapper.UserIdentityMapper;
import com.minibank.UserService.Repository.UserIdentityRepository;
import com.minibank.UserService.Repository.UserRepository;
import com.minibank.UserService.dto.Request.UserIdentity.UserIdentityRequest;
import com.minibank.UserService.dto.Response.UserIdentityResponse;
import static com.minibank.UserService.util.SecurityUtil.getCurrentUserId;

import jakarta.transaction.Transactional;

@Service
public class UserIdentityService {
    @Autowired
    private UserIdentityRepository userIdentityRepository;
    @Autowired
    private MinioService minioService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountServiceClient accountServiceClient;
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public UserIdentityResponse createUserIdentity(UserIdentityRequest request) {
        Long userId = getCurrentUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        userIdentityRepository.findByUserId(userId).ifPresent(u -> {throw new AppException(ErrorCode.IDENTITY_ALREADY_EXISTS);});
        try {
            MultipartFile front = request.getIdCardFront();
            MultipartFile back = request.getIdCardBack();
            String frontUrl = minioService.uploadFile(
                        UUID.randomUUID() + "-" + front.getOriginalFilename(),
                        front.getInputStream(),
                        front.getContentType(),
                        front.getSize());
                String backUrl = minioService.uploadFile(
                        UUID.randomUUID() + "-" + back.getOriginalFilename(),
                        back.getInputStream(),
                        back.getContentType(),
                        back.getSize());
            UserIdentityEntity userIdentity = new UserIdentityEntity();
            userIdentity.setFullName(request.getFullName());
            userIdentity.setIdCardNumber(request.getIdCardNumber());
            userIdentity.setDateOfBirth(request.getDateOfBirth());
            userIdentity.setIdCardFrontUrl(frontUrl);
            userIdentity.setIdCardBackUrl(backUrl);
            userIdentity.setIssueDate(request.getIssueDate());
            userIdentity.setKycStatus(KycStatus.PENDING);
            userIdentity.setSubmittedAt(Instant.now());
            userIdentity.setUser(user);
            return UserIdentityMapper.toResponse(userIdentityRepository.save(userIdentity));
        } catch (Exception e) {
            throw new AppException(ErrorCode.IDENTITY_UPLOAD_FAILED);
        }
    }
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public UserIdentityResponse updateIdentity(UserIdentityRequest request) {
        try {
            Long userId = getCurrentUserId();
            UserIdentityEntity userIdentity = userIdentityRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.IDENTITY_NOT_FOUND));
            if (userIdentity.getKycStatus() == KycStatus.APPROVED) {
                throw new AppException(ErrorCode.IDENTITY_ALREADY_VERIFIED);
            }
            userIdentity.setFullName(request.getFullName());
            userIdentity.setIdCardNumber(request.getIdCardNumber());
            userIdentity.setDateOfBirth(request.getDateOfBirth());
            userIdentity.setIssueDate(request.getIssueDate());
            MultipartFile front = request.getIdCardFront();
            if (front != null && !front.isEmpty()) {
                String frontUrl = minioService.uploadFile(
                        UUID.randomUUID() + "-" + front.getOriginalFilename(),
                        front.getInputStream(),
                        front.getContentType(),
                        front.getSize());
                userIdentity.setIdCardFrontUrl(frontUrl);
            }
            MultipartFile back = request.getIdCardBack();
            if (back != null && !back.isEmpty()) {
                String backUrl = minioService.uploadFile(
                        UUID.randomUUID() + "-" + back.getOriginalFilename(),
                        back.getInputStream(),
                        back.getContentType(),
                        back.getSize());
                userIdentity.setIdCardBackUrl(backUrl);
            }
            userIdentity.setKycStatus(KycStatus.PENDING);
            userIdentity.setSubmittedAt(Instant.now());
            userIdentity.setVerifiedAt(null);
            return UserIdentityMapper.toResponse(userIdentityRepository.save(userIdentity));
        }
        catch(Exception e) {
            throw new AppException(ErrorCode.IDENTITY_UPLOAD_FAILED);
        }
    }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserIdentityResponse updateStatus(Long id, KycStatus status) {
        UserIdentityEntity userIdentity = userIdentityRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.IDENTITY_NOT_FOUND));
        userIdentity.setKycStatus(status);
        userIdentity.setVerifiedAt(Instant.now());
        if(status == KycStatus.APPROVED) {
            Long userId = userIdentity.getUser().getId();
            accountServiceClient.sendActivationRequest(userId);
        }
        return UserIdentityMapper.toResponse(userIdentityRepository.save(userIdentity));
    }
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public UserIdentityResponse getMyIdentity() {
        Long userId = getCurrentUserId();
        UserIdentityEntity userIdentity = userIdentityRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.IDENTITY_NOT_FOUND));
        return UserIdentityMapper.toResponse(userIdentity);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public UserIdentityResponse getById(Long id) {
        UserIdentityEntity userIdentity = userIdentityRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.IDENTITY_NOT_FOUND));
        return UserIdentityMapper.toResponse(userIdentity);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public List<UserIdentityResponse> getPendingIdentities() {
        return userIdentityRepository.findByKycStatus(KycStatus.PENDING)
            .stream()
            .map(UserIdentityMapper::toResponse)
            .collect(Collectors.toList());
    }
    public KycStatus getKycStatusByUserId(Long userId) {
        return userIdentityRepository.findByUserId(userId).map(UserIdentityEntity::getKycStatus).orElse(KycStatus.PENDING); 
    }
}
