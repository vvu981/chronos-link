package com.vvu981.chronoslink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. DESACTIVAR CSRF: Obligatorio para APIs REST que usan POST/PUT
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 2. PUERTAS ABIERTAS: Registro y Login
                        // Permitimos POST a /api/v1/users (registro) y cualquier cosa en /auth/
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 3. PUERTAS CERRADAS: Todo lo demás requiere estar autenticado
                        .anyRequest().authenticated()
                )

                // 4. MODO API: Desactivamos el "formulario raro" y usamos autenticación básica
                // Esto evita que el navegador te redirija a una página HTML de login
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}