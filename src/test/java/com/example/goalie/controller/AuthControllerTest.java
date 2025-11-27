package com.example.goalie.controller;

import com.example.goalie.model.User;
import com.example.goalie.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // rollback after each test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final String existingEmail = "existinguser@example.com";

    @BeforeEach
    void setUp() {
        // ensure a test user exists for login tests
        if (!userRepository.findByEmail(existingEmail).isPresent()) {
            User u = new User();
            u.setName("Existing User");
            u.setEmail(existingEmail);
            u.setPassword("{noop}password123"); // store plain password for test
            userRepository.save(u);
        }
    }

    @Test
    void signupMissingName_shouldReturnError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "newuser@example.com")
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user", "name"));
    }

    @Test
    void signupMissingEmail_shouldReturnError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("name", "New User")
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));
    }

    @Test
    void loginWithInvalidCredentials_shouldFail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", existingEmail)
                        .param("password", "wrongpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error")); // assuming your login failure redirects here
    }

    @Test
    void loginWithValidCredentials_shouldSucceed() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", existingEmail)
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home")); // assuming successful login redirects to /home
    }
}
