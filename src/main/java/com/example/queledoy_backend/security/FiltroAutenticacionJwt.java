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


public class FiltroAutenticacionJwt extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FiltroAutenticacionJwt.class);
    private static final String ENCABEZADO_AUTORIZACION = "Authorization";
    private static final String PREFIJO_PORTADOR = "Bearer ";
    private static final String PREFIJO_ROL = "ROLE_";

    private final ProveedorTokenJwt proveedorToken;

    public FiltroAutenticacionJwt(ProveedorTokenJwt proveedorToken) {
        this.proveedorToken = proveedorToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extraer token del header
            String jwt = extraerTokenDelRequest(request);
            
            // Si hay token y es válido, autenticar
            if (StringUtils.hasText(jwt) && proveedorToken.validarToken(jwt)) {
                // Extraer información del usuario
                String emailUsuario = proveedorToken.obtenerEmailDelToken(jwt);
                String rol = proveedorToken.obtenerRolDelToken(jwt);
                Integer idUsuario = proveedorToken.obtenerIdDelToken(jwt);
                
                if (emailUsuario != null) {

                    SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority(PREFIJO_ROL + rol);

                    UsernamePasswordAuthenticationToken autenticacion = 
                        new UsernamePasswordAuthenticationToken(
                            emailUsuario,
                            null,
                            Collections.singletonList(autoridad)
                        );
                    
                    autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                    
                    logger.debug("Autenticación JWT establecida para: {} con rol: {}", emailUsuario, rol);
                }
            }
        } catch (Exception ex) {
            logger.debug("Error procesando JWT: {}", ex.getMessage());
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    private String extraerTokenDelRequest(HttpServletRequest request) {
        String encabezadoAutorizacion = request.getHeader(ENCABEZADO_AUTORIZACION);
        
        if (StringUtils.hasText(encabezadoAutorizacion) && encabezadoAutorizacion.startsWith(PREFIJO_PORTADOR)) {
            return encabezadoAutorizacion.substring(PREFIJO_PORTADOR.length());
        }
        
        return null;
    }
}
