package com.nexusmart.api.controller;

import com.nexusmart.api.dto.UpdateUserRequestDTO;
import com.nexusmart.api.dto.UserRegistrationRequestDTO;
import com.nexusmart.api.dto.UserResponseDTO;
import com.nexusmart.api.entity.User;
import com.nexusmart.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Injecting the UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO requestDTO) {
        User createdUser = userService.createUser(requestDTO);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(createdUser.getId());
        responseDTO.setEmail(createdUser.getEmail());
        responseDTO.setFirstName(createdUser.getFirstName());
        responseDTO.setLastName(createdUser.getLastName());
        responseDTO.setCreatedAt(createdUser.getCreatedAt());

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id:[\\d]+}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        // 1. Call the service to get the User entity
        User user = userService.getUserById(id);

        // 2. Convert the User entity to our "safe" response DTO
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setFirstName(user.getFirstName());
        responseDTO.setLastName(user.getLastName());
        responseDTO.setCreatedAt(user.getCreatedAt());
        responseDTO.setUpdatedAt(user.getUpdatedAt());

        // 3. Return the DTO with a 200 OK status
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id:[\\d]+}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO requestDTO) {
        // 1. Call the service to update the user
        User updatedUser = userService.updateUser(id, requestDTO);

        // 2. Convert the updated User entity to our "safe" response DTO
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(updatedUser.getId());
        responseDTO.setEmail(updatedUser.getEmail());
        responseDTO.setFirstName(updatedUser.getFirstName());
        responseDTO.setLastName(updatedUser.getLastName());
        responseDTO.setCreatedAt(updatedUser.getCreatedAt()); // You might want to return updatedAt here too!
        responseDTO.setUpdatedAt(updatedUser.getUpdatedAt());

        // 3. Return the DTO with a 200 OK status
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}