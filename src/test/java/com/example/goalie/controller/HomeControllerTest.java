package com.example.goalie.controller;

import com.example.goalie.config.AppService;
import com.example.goalie.model.User;
import com.example.goalie.model.UserProfile;
import com.example.goalie.repository.UserProfileRepository;
import com.example.goalie.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebMvcTest
class HomeControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void signupMissingName_shouldReturnError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user", "name"));
    }

    @Test
    void signupMissingEmail_shouldReturnError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("name", "Test User")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));
    }

    @Test
    void loginInvalidCredentials_shouldReturnError() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "wrong@example.com")
                        .param("password", "wrongpass"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }


    @Test
    void setupPlayerProfile() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(new UserProfile());
        profile.setUser(user);
        profile.setPlayerNickname("TestNickname");
        profile.setSkillRating(3);
        userProfileRepository.save(profile);

        UserProfile fetched = userProfileRepository.findByUser(user).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getPlayerNickname()).isEqualTo("TestNickname");

//        userProfileRepository.delete(fetched);
//        userRepository.delete(user);
    }


    @Test
    void editProfile() {
        String email = "testuser@example.com";
        User user = userRepository.findByEmail(email).orElse(null);
        assertThat(user).isNotNull();

        String originalName = user.getName();

        user.setName("Updated Name");
        userRepository.save(user);

        User updated = userRepository.findByEmail(email).orElse(null);
        assertThat(updated.getName()).isEqualTo("Updated Name");

        updated.setName(originalName);
        userRepository.save(updated);
    }
}