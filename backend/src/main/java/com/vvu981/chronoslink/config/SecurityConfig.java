package com.vvu981.chronoslink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactivamos esto para poder usar Postman fácil
                .authorizeHttpRequests(auth -> auth
                        // Permitimos que cualquiera se registre (POST /users)
                        .requestMatchers("/api/v1/users").permitAll()
                        // El resto de cosas, hay que estar logueado
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // Usaremos login básico (usuario/pass) por ahora

        return http.build();
    }
}