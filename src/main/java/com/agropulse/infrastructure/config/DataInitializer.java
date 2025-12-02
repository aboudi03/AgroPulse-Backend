package com.agropulse.infrastructure.config;

import com.agropulse.application.service.AuthService;
import com.agropulse.domain.model.Farm;
import com.agropulse.domain.model.User;
import com.agropulse.domain.repository.FarmRepository;
import com.agropulse.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    public DataInitializer(AuthService authService, FarmRepository farmRepository, UserRepository userRepository) {
        this.authService = authService;
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer started.");
        try {
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                System.out.println("Initializing Super Admin User...");

                // Create a default System Farm if not exists
                Farm systemFarm = farmRepository.findAll().stream()
                        .filter(f -> "System Farm".equals(f.getName()))
                        .findFirst()
                        .orElseGet(() -> {
                            System.out.println("Creating System Farm...");
                            return farmRepository.save(new Farm("System Farm"));
                        });

                // Create Admin User
                authService.register(
                        adminUsername,
                        adminPassword,
                        adminEmail,
                        systemFarm.getId(),
                        User.Role.ADMIN);

                System.out.println("Super Admin initialized successfully.");
                System.out.println("Username: " + adminUsername);
                // Do not log password in production, but acceptable for this debug session if
                // local
                System.out.println("Password: " + adminPassword);
            } else {
                System.out.println("Super Admin already exists.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
