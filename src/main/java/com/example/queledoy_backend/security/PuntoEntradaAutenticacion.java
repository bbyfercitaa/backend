package com.example.queledoy_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Maneja errores de autenticación (401)
 * Se invoca cuando un usuario intenta acceder sin autenticarse
 */
public class PuntoEntradaAutenticacion implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(PuntoEntradaAutenticacion.class);

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException excepcion) throws IOException, ServletException {
        logger.warn("Acceso denegado - sin autenticación. Ruta: {}", request.getServletPath());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("estado", HttpServletResponse.SC_UNAUTHORIZED);
        cuerpo.put("error", "No autorizado");
        cuerpo.put("mensaje", "Token JWT inválido o ausente");
        cuerpo.put("ruta", request.getServletPath());

        final ObjectMapper mapeador = new ObjectMapper();
        mapeador.writeValue(response.getOutputStream(), cuerpo);
    }
}
