package com.example.queledoy_backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


/**
 * Filtro JWT que valida tokens en cada HTTP request
 * 
 * Responsabilidades:
 * 1. Extraer token JWT del header Authorization
 * 2. Validar que el token sea válido
 * 3. Extraer información del usuario (email, rol, ID)
 * 4. Establecer autenticación en SecurityContext
 * 5. Propagar la cadena de filtros
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtTokenProvider tokenProvider;

    public JwtAuthFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Procesa cada request para validar JWT
     * Se ejecuta una sola vez por request (OncePerRequestFilter)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extraer token del header
            String jwt = extractJwtFromRequest(request);
            
            // Si hay token y es válido, autenticar
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Extraer información del usuario del token
                String userEmail = tokenProvider.getUserEmailFromToken(jwt);
                String rol = tokenProvider.getRolFromToken(jwt);
                Integer userId = tokenProvider.getUserIdFromToken(jwt);
                
                if (userEmail != null) {
                    // Crear autoridades con el rol
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(ROLE_PREFIX + rol);
                    
                    // Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            Collections.singletonList(authority)
                        );
                    
                    // Establecer detalles del request
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecer autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.debug("Autenticación JWT establecida para usuario: {} con rol: {}", userEmail, rol);
                }
            }
        } catch (Exception ex) {
            // No propagar la excepción - dejar que el siguiente filtro maneje
            logger.debug("Error procesando JWT: {}", ex.getMessage());
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     * 
     * Formato esperado: Authorization: Bearer <token>
     * 
     * @param request el HTTP request
     * @return token sin el prefijo "Bearer " o null si no existe
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            // Extraer token removiendo "Bearer "
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
}
