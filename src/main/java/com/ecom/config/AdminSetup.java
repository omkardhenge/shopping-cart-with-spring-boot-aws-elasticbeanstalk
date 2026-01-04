package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSetup {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdminIfNotExists() {
        String adminEmail = "admin@gmail.com";

        if (userRepository.findByEmail(adminEmail) == null) {
            UserDtls admin = new UserDtls();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123")); // BCrypt encoded
            admin.setRole("ROLE_ADMIN");
            admin.setIsEnable(true);
            admin.setAccountNonLocked(true);
            admin.setFailedAttempt(0);
            admin.setProfileImage(null); // optional
            userRepository.save(admin);
            System.out.println("Admin created: " + adminEmail);
        } else {
            System.out.println("Admin already exists: " + adminEmail);
        }
    }
}
