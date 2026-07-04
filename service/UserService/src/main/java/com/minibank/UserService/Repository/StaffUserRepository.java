package com.minibank.UserService.Repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minibank.UserService.Entity.StaffUserEntity;
import com.minibank.UserService.Enum.StaffRole;
import com.minibank.UserService.Enum.StaffStatus;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUserEntity, Long>{
    Optional<StaffUserEntity> findByEmail(String email);
    List<StaffUserEntity> findByRole(StaffRole role);
    List<StaffUserEntity> findByStatus(StaffStatus status);
}
