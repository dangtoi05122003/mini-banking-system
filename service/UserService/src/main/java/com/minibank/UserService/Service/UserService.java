package com.minibank.UserService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minibank.UserService.Entity.UserEntity;
import com.minibank.UserService.Enum.UserStatus;
import com.minibank.UserService.Mapper.UserMapper;
import com.minibank.UserService.Repository.UserRepository;
import com.minibank.UserService.dto.Request.User.UserRequest;
import com.minibank.UserService.dto.Response.UserResponse;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    public UserResponse createUser(UserRequest request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        return UserMapper.toResponse(userRepository.save(user));
    }
}
