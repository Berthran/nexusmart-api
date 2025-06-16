package com.nexusmart.api.service;

import com.nexusmart.api.dto.UpdateUserRequestDTO;
import com.nexusmart.api.dto.UserRegistrationRequestDTO;
import com.nexusmart.api.entity.User;
import com.nexusmart.api.exception.ResourceNotFoundException;
import com.nexusmart.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRegistrationRequestDTO requestDTO) {
        User newUser = new User();
        newUser.setEmail(requestDTO.getEmail());
        newUser.setFirstName(requestDTO.getFirstName());
        newUser.setLastName(requestDTO.getLastName());

        newUser.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));

        return userRepository.save(newUser);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public User updateUser(Long userId, UpdateUserRequestDTO requestDTO) {
        // 1. Find the existing user, or throw an exception
        User existingUser = this.getUserById(userId);

        // 2. Modify the fields on the found entity
        existingUser.setFirstName(requestDTO.getFirstName());
        existingUser.setLastName(requestDTO.getLastName());
        existingUser.setPhoneNumber(requestDTO.getPhoneNumber());

        // 3. Save the updated entity. JPA is smart enough to know this is an UPDATE.
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        // 1. First, check if the user exists.
        // Our getUserById method already handles throwing an exception if not found.
        this.getUserById(userId);

        // 2. If the user exists, delete them.
        userRepository.deleteById(userId);
    }

}