package com.fixmycar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Включаем CORS с нашей конфигурацией
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Отключаем CSRF, чтобы фронтенд мог делать POST/PUT/DELETE без токена
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Разрешаем все запросы (пока без авторизации, можно потом добавить JWT)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Заголовки, чтобы H2 console (если используется) работала
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔹 Добавьте localhost:3000 в разрешенные origins!
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",          // ← ДОБАВЬТЕ ЭТО!
                "http://127.0.0.1:3000",
                "http://192.168.1.*:8080",
                "http://192.168.1.*:3000",
                "http://192.168.10.100:3000",    // если нужно
                "https://fixmycar-frontend.onrender.com"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
