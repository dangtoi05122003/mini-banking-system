package com.minibank.UserService.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minibank.UserService.Entity.UserEntity;

@Repository
public interface UserRepository extends  JpaRepository<UserEntity, Long>{
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
}
