
package com.trustvault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class UserInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${TRUSTVAULT_USERNAME:}")
    private String username;

    @Value("${TRUSTVAULT_PASSWORD:}")
    private String password;

    public UserInitializer(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException(
                    "TrustVault account environment variables are missing");
        }

        AppUser user = userRepository.findByUsername(username)
                .orElseGet(AppUser::new);

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ADMIN");

        userRepository.save(user);
    }
}
