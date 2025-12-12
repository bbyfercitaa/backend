package com.example.queledoy_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


/**
 * Configuración de seguridad con JWT
 * 
 * Características:
 * - Autenticación sin estado (stateless)
 * - Protección con JWT
 * - CORS configurado para desarrollo
 * - CSRF deshabilitado para API REST
 * - Acceso público a autenticación
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
public class ConfiguracionSeguridad {

    @Autowired
    private ProveedorTokenJwt proveedorTokenJwt;

    /**
     * Filtro de autenticación JWT
     */
    @Bean
    public FiltroAutenticacionJwt filtroAutenticacionJwt() {
        return new FiltroAutenticacionJwt(proveedorTokenJwt);
    }

    /**
     * Configuración del filtro de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain cadeneFiltrosSeguridad(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para API REST (stateless con JWT)
            .csrf().disable()
            
            // Configurar CORS
            .cors().and()
            
            // Sesiones sin estado
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            
            // Configurar autorización
            .authorizeRequests()
                // Endpoints públicos de autenticación
                .antMatchers("/api/v1/auth/login").permitAll()
                .antMatchers("/api/v1/auth/registro").permitAll()
                
                // Endpoints para usuarios autenticados
                .antMatchers(HttpMethod.POST, "/api/v1/auth/refresh-token").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                
                // Endpoints administrativos
                .antMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")
                
                .antMatchers(HttpMethod.POST, "/api/v1/categorias").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/v1/categorias/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/v1/categorias/**").hasRole("ADMIN")
                
                // Resto requiere autenticación
                .anyRequest().authenticated()
            .and()
            
            // Manejo de excepciones
            .exceptionHandling()
                .authenticationEntryPoint(new PuntoEntradaAutenticacion())
                .accessDeniedHandler(new ManejadorAccesoDenegado())
            .and()
            
            // Agregar filtro JWT
            .addFilterBefore(filtroAutenticacionJwt(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Gestor de autenticación
     */
    @Bean
    public AuthenticationManager gestorAutenticacion(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Codificador de contraseñas BCrypt (fuerza: 10)
     */
    @Bean
    public PasswordEncoder codificadorContrasenas() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configuración de CORS
     */
    @Bean
    public CorsConfigurationSource fuenteConfiguracionCors() {
        CorsConfiguration configuracion = new CorsConfiguration();
        
        configuracion.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://quele-doy.vercel.app"
        ));
        
        configuracion.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuracion.setAllowedHeaders(Arrays.asList("*"));
        configuracion.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuracion.setAllowCredentials(true);
        configuracion.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        
        return fuente;
    }
}

