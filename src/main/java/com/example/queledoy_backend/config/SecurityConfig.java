package com.example.queledoy_backend.config;

import com.example.queledoy_backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/api/v1/usuarios/login",
                    "/api/v1/usuarios/register",
                    "/api/v1/usuarios",
                    "/api/v1/productos/**",
                    "/api/v1/categorias/**",
                    "/api/v1/categoria/**",
                    "/api/v1/colores/**",
                    "/api/v1/color/**",
                    "/api/v1/generos/**",
                    "/api/v1/genero/**",
                    "/api/v1/imagenes/**",
                    "/api/v1/imagen/**",
                    "/api/v1/listas/**",
                    "/doc/swagger-ui.html",
                    "/doc/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/h2-console/**"
                ).permitAll()
                
                // Endpoints solo para ADMIN
                .requestMatchers(
                    "/api/v1/roles/**"
                ).hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}