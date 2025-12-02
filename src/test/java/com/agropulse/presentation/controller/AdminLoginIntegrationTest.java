package com.agropulse.presentation.controller;

import com.agropulse.domain.model.User;
import com.agropulse.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Test
    public void testAdminUserExistsAndCanLogin() throws Exception {
        // 1. Verify Admin User Exists
        Optional<User> adminUserOpt = userRepository.findByUsername(adminUsername);
        assertTrue(adminUserOpt.isPresent(), "Admin user should exist in the database");

        User adminUser = adminUserOpt.get();

        // 2. Verify Password Matches
        assertTrue(passwordEncoder.matches(adminPassword, adminUser.getPassword()),
                "Stored password should match the configured admin password");

        // 3. Attempt Login via API
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", adminUsername);
        loginRequest.put("password", adminPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
