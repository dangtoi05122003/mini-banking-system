package com.minibank.UserService.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minibank.UserService.Entity.StaffUserEntity;
import com.minibank.UserService.Enum.StaffRole;
import com.minibank.UserService.Enum.StaffStatus;
import com.minibank.UserService.Exception.AppException;
import com.minibank.UserService.Exception.ErrorCode;
import com.minibank.UserService.Mapper.StaffUserMapper;
import com.minibank.UserService.Repository.StaffUserRepository;
import com.minibank.UserService.dto.Request.StaffUser.StaffUserRequest;
import com.minibank.UserService.dto.Request.StaffUser.UpdateStaffRequest;
import com.minibank.UserService.dto.Request.User.ChangePasswordRequest;
import com.minibank.UserService.dto.Response.StaffUserResponse;
import static com.minibank.UserService.util.SecurityUtil.getCurrentUserId;

@Service
public class StaffUserService {
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PreAuthorize("hasRole('ADMIN')")
    public StaffUserResponse createStaff(StaffUserRequest request) {
        StaffUserEntity staff = new StaffUserEntity();
        staff.setUsername(request.getUsername());
        staff.setEmail(request.getEmail());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setRole(StaffRole.TELLER);
        staff.setStatus(StaffStatus.ACTIVE);
        return StaffUserMapper.toResponse(staffUserRepository.save(staff));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public StaffUserResponse getStaffById(Long id) {
        StaffUserEntity staff = staffUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        return StaffUserMapper.toResponse(staff);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public StaffUserResponse lockStaff(Long id) {
        StaffUserEntity staff = staffUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (staff.getStatus() == StaffStatus.TERMINATED) {
            throw new AppException(ErrorCode.STAFF_TERMINATED);
        }
        if (staff.getStatus() == StaffStatus.LOCKED) {
            throw new AppException(ErrorCode.STAFF_LOCKED);
        }
        staff.setStatus(StaffStatus.LOCKED);
        return StaffUserMapper.toResponse(staffUserRepository.save(staff));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public StaffUserResponse unlockStaff(Long id) {
        StaffUserEntity staff = staffUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (staff.getStatus() == StaffStatus.TERMINATED) {
            throw new AppException(ErrorCode.STAFF_TERMINATED);
        }
        if (staff.getStatus() != StaffStatus.LOCKED) {
            throw new AppException(ErrorCode.STAFF_NOT_LOCKED);
        }
        staff.setStatus(StaffStatus.ACTIVE);
        return StaffUserMapper.toResponse(staffUserRepository.save(staff));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public StaffUserResponse changePassword(ChangePasswordRequest request) {
        Long staffId = getCurrentUserId();
        StaffUserEntity staff = staffUserRepository.findById(staffId).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        validateUserStatus(staff);
        if (!passwordEncoder.matches(request.getOldPassword(), staff.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
        staff.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return StaffUserMapper.toResponse(staffUserRepository.save(staff));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public StaffUserResponse changeRole(Long id, StaffRole role) {
        StaffUserEntity staff = staffUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        staff.setRole(role);
        return StaffUserMapper.toResponse(staffUserRepository.save(staff));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffUserResponse> getAllStaff() {
        return staffUserRepository.findAll()
            .stream()
            .map(StaffUserMapper::toResponse)
            .collect(Collectors.toList());
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public StaffUserResponse updateStaff(UpdateStaffRequest request) {
        Long staffId = getCurrentUserId();
        StaffUserEntity staff = staffUserRepository.findById(staffId).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        validateUserStatus(staff);
        staff.setUsername(request.getUsername());
        staff.setEmail(request.getEmail());
        return StaffUserMapper.toResponse(staffUserRepository.save(staff));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public StaffUserResponse deleteStaff(Long id) {
        StaffUserEntity staff = staffUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        if (staff.getStatus() == StaffStatus.TERMINATED) {
            throw new AppException(ErrorCode.STAFF_TERMINATED);
        }
        staff.setStatus(StaffStatus.TERMINATED);
        return StaffUserMapper.toResponse(staffUserRepository.save(staff));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public StaffUserResponse getMyInfo() {
        Long id = getCurrentUserId();
        StaffUserEntity staff = staffUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
        validateUserStatus(staff);
        return StaffUserMapper.toResponse(staff);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffUserResponse> getStaffByRole(StaffRole role) {
        return staffUserRepository.findByRole(role)
                .stream()
                .map(StaffUserMapper::toResponse)
                .collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffUserResponse> getStaffByStatus(StaffStatus status) {
        return staffUserRepository.findByStatus(status)
                .stream()
                .map(StaffUserMapper::toResponse)
                .collect(Collectors.toList());
    }
    private void validateUserStatus(StaffUserEntity staff) {
        if (staff.getStatus() == StaffStatus.LOCKED) {
            throw new AppException(ErrorCode.STAFF_LOCKED);
        }
        if (staff.getStatus() == StaffStatus.TERMINATED) {
            throw new AppException(ErrorCode.STAFF_TERMINATED);
        }
        if (staff.getStatus() != StaffStatus.ACTIVE) {
            throw new AppException(ErrorCode.STAFF_NOT_ACTIVE);
        }
    }
}
