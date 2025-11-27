package com.example.goalie.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AppService appService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(appService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public URLs
                        .requestMatchers("/", "/signup", "/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        // Any other URL requires login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                                .failureHandler((request, response, exception) -> {
                                    System.out.println("Login failed: " + exception.getMessage());
                                    response.sendRedirect("/login?error"); // redirect back to login page with error
                                })
//                        .defaultSuccessUrl("/home", true) // redirect to admin after login
                                .successHandler((request, response, authentication) -> {
                                    // Get the username/email
                                    String email = authentication.getName();// this is the username/email used to login
                                    System.out.println(email);
                                    // Redirect based on email
                                    if ("admin@admin.com".equals(email)) {
                                        response.sendRedirect("/admin");
                                        System.out.println("redirected to admin");// admin dashboard
                                    } else {
                                        response.sendRedirect("/home");
                                        System.out.println("redirected to home");// regular user dashboard
                                    }
                                })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // for H2 console

        return http.build();
    }
}
