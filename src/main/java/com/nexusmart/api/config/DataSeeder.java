package com.nexusmart.api.config;


import com.nexusmart.api.entity.Role;
import com.nexusmart.api.entity.User;
import com.nexusmart.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an ADMIN user already exists
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            // If not, create a new admin user
            User admin = new User();
            admin.setEmail("admin@nexusmart.com");
            admin.setPasswordHash(passwordEncoder.encode("admin1234")); // Use a strong password in production!
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println("Created ADMIN user");;
        }
    }
}
