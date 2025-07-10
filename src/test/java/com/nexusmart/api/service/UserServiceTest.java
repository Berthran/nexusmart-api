package com.nexusmart.api.service;

import com.nexusmart.api.dto.UserRegistrationRequestDTO;
import com.nexusmart.api.entity.Role;
import com.nexusmart.api.entity.User;
import com.nexusmart.api.exception.ResourceConflictException;
import com.nexusmart.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// This annotation tells JUnit 5 to enable Mockito support
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    // @Mock creates a fake version of the UserRepository/
    // We can program its behavior in our tests.
    @Mock
    private UserRepository userRepository;

    // @Mock creates a fake version of the PasswordEncoder
    @Mock
    private PasswordEncoder passwordEncoder;

    // @InjectMocks creates a real instance of UserService, but
    // automatically injects the mocks created above (@Mock) into it.
    @InjectMocks
    private UserService userService;

    // We will write our tests methods here...
    @Test
    void createUser_shouldSaveAndReturnUser_whenEmailIsUnique() {
        // -- ARRANGE ---

        // 1. Create the input data for our service method
        UserRegistrationRequestDTO requestDTO = new UserRegistrationRequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("password123");
        requestDTO.setFirstName("Test");
        requestDTO.setLastName("User");

        // 2. Program our mocks' behaviour
        // When userRepository.findByEmail() is called with any string,
        // pretend the user doesn't exist by returning an empty Optional.
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When passwordEncoder.encode() is called, return a predictable hashed password.
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password_string");

        // When userRepository.save() is called with any User object,
        // just return that same object back to us.
        when(userRepository.save(any(User.class))).thenAnswer(invocation ->invocation.getArgument(0));

        // --- ACT ---

        // 3. Execute the method we are testing
        User savedUser = userService.createUser(requestDTO);

        // --- ASSERT ---

        // 4. Verify that the results are what we expect
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed_password_string");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void createUser_shouldThrowConflictException_whenEmailExists() {
        // --- ARRANGE ---

        // 1. Create the input data
        UserRegistrationRequestDTO requestDTO = new UserRegistrationRequestDTO();
        requestDTO.setEmail("existing@example.com");
        requestDTO.setPassword("password123");

        // 2. Create a dummy User object to simulate the user that already exists
        User existingUser = new User();

        // 3. Program the mock repository to find the existing user
        // When findByEmail is called, return an Optional containing our dummy user.
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // --- ACT & ASSERT ---

        // 4. We assert that executing the createUser method throws the exception we expect.
        // assertThrows takes the expected Exception class and a lambda containing the code to execute.
        assertThrows(ResourceConflictException.class, () -> {
            userService.createUser(requestDTO);
        });

        // 5. As an extra check, we can verify that the save method was never called.
        verify(userRepository, never()).save(any(User.class));


    }
}
