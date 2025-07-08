package com.nexusmart.api.repository;

import com.nexusmart.api.entity.Role;
import com.nexusmart.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByRole(Role role);
}