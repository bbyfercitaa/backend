package com.example.queledoy_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Maneja errores de autorización (403)
 * Se invoca cuando un usuario sin permisos intenta acceder a un recurso
 */
public class ManejadorAccesoDenegado implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(ManejadorAccesoDenegado.class);

    @Override
    public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      AccessDeniedException excepcion) throws IOException, ServletException {
        logger.warn("Acceso denegado - permisos insuficientes. Ruta: {}", request.getServletPath());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("estado", HttpServletResponse.SC_FORBIDDEN);
        cuerpo.put("error", "Acceso denegado");
        cuerpo.put("mensaje", "No tienes permiso para acceder a este recurso");
        cuerpo.put("ruta", request.getServletPath());

        final ObjectMapper mapeador = new ObjectMapper();
        mapeador.writeValue(response.getOutputStream(), cuerpo);
    }
}
