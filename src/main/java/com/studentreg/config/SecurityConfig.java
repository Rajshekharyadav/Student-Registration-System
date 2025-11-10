package com.studentreg.config;

import com.studentreg.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .antMatchers("/", "/login", "/create-account", "/register", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/admin", "/admin/**").hasRole("ADMIN")
                .antMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    try {
                        String role = authentication.getAuthorities().iterator().next().getAuthority();
                        if (role.equals("ROLE_ADMIN")) {
                            response.sendRedirect("/admin");
                        } else if (role.equals("ROLE_STUDENT")) {
                            response.sendRedirect("/student/dashboard");
                        } else {
                            response.sendRedirect("/");
                        }
                    } catch (Exception e) {
                        response.sendRedirect("/");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            .csrf().disable()
            .headers().frameOptions().disable();
        
        return http.build();
    }
}