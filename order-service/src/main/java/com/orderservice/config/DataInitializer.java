package com.orderservice.config;

import com.orderservice.model.AppUser;
import com.orderservice.model.Role;
import com.orderservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // admin/admin123
        userRepository.findByUsername("admin").orElseGet(() ->
                userRepository.save(new AppUser("admin", passwordEncoder.encode("admin123"), Role.ADMIN))
        );

        // keerthana/test123
        userRepository.findByUsername("keerthana").orElseGet(() ->
                userRepository.save(new AppUser("keerthana", passwordEncoder.encode("test123"), Role.USER))
        );
    }
}
