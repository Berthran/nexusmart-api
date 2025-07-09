package com.nexusmart.api.service;

import com.nexusmart.api.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
