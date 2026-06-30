package com.minibank.UserService.Repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minibank.UserService.Entity.UserIdentityEntity;
import com.minibank.UserService.Enum.KycStatus;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentityEntity, Long>{
    Optional<UserIdentityEntity> findByUserId(Long userId);
    List<UserIdentityEntity> findByKycStatus(KycStatus status);
}
