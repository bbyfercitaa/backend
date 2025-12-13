package com.example.queledoy_backend.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.queledoy_backend.model.Usuario;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Proveedor seguro de tokens JWT
 * 
 * Responsabilidades:
 * - Generar tokens JWT con claims de usuario
 * - Validar tokens (firma, expiración, formato)
 * - Renovar tokens de manera segura
 * - Extraer información del usuario desde tokens
 * 
 * Seguridad:
 * - Valida longitud mínima de clave secreta (64 caracteres)
 * - Usa HS256 con UTF-8
 * - No expone información sensible en logs
 * - Manejo robusto de excepciones
 */
@Component
public class ProveedorTokenJwt {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorTokenJwt.class);
    private static final int LONGITUD_MINIMA_SECRETO = 64; // Mínimo 64 caracteres para HS256
    private static final long BUFFER_RENOVACION_TOKEN = 3600000; // 1 hora de buffer

    @Value("${jwt.secret:}")
    private String secretoJwt;

    @Value("${jwt.expiration:86400000}") 
    private long duracionToken;

    private SecretKey claveFirma;

    /**
     * Obtiene la clave de firma con validación de seguridad
     */
    private SecretKey obtenerClaveFirma() {
        if (claveFirma == null) {
            validarSecreto();
            claveFirma = Keys.hmacShaKeyFor(secretoJwt.getBytes(StandardCharsets.UTF_8));
        }
        return claveFirma;
    }

    /**
     * Valida que la clave secreta cumple estándares de seguridad
     */
    private void validarSecreto() {
        if (secretoJwt == null || secretoJwt.isBlank()) {
            throw new IllegalArgumentException(
                "jwt.secret no configurado. Configure en application.properties con mínimo 64 caracteres"
            );
        }
        if (secretoJwt.length() < LONGITUD_MINIMA_SECRETO) {
            throw new IllegalArgumentException(
                String.format("jwt.secret muy corta. Mínimo %d caracteres, actual: %d", 
                    LONGITUD_MINIMA_SECRETO, secretoJwt.length())
            );
        }
    }

    /**
     * Genera un token JWT para un usuario
     */
    public String generarToken(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }
        if (usuario.getId() == null || usuario.getCorreo() == null) {
            throw new IllegalArgumentException("Usuario debe tener ID y correo");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("nombre", usuario.getNombre());
        claims.put("email", usuario.getCorreo());
        
        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            claims.put("rol", usuario.getRol().getNombre());
        } else {
            claims.put("rol", "USER");
        }

        return crearToken(claims, usuario.getCorreo());
    }

    /**
     * Crea un token JWT con issuance y expiración
     */
    private String crearToken(Map<String, Object> claims, String asunto) {
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + duracionToken);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(asunto)
                .setIssuedAt(ahora)
                .setExpiration(fechaExpiracion)
                .signWith(obtenerClaveFirma(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida un token JWT verificando:
     * - Firma (detección de tampering)
     * - Expiración
     * - Formato válido
     * - Claims requeridos
     */
    public boolean validarToken(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("Intento de validar token vacío");
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(obtenerClaveFirma())
                    .build()
                    .parseClaimsJws(token);
            
            logger.debug("Token JWT validado exitosamente");
            return true;
            
        } catch (ExpiredJwtException e) {
            logger.warn("Token JWT expirado");
            return false;
            
        } catch (SecurityException | io.jsonwebtoken.security.SecurityException e) {
            logger.warn("Firma JWT inválida - posible tampering");
            return false;
            
        } catch (MalformedJwtException e) {
            logger.warn("Token JWT mal formado");
            return false;
            
        } catch (UnsupportedJwtException e) {
            logger.warn("Token JWT no soportado");
            return false;
            
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims vacío");
            return false;
            
        } catch (Exception e) {
            logger.warn("Error validando token JWT: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    /**
     * Extrae el email del token
     */
    public String obtenerEmailDelToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(obtenerClaveFirma())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            
            if (email == null || email.isBlank()) {
                logger.warn("Email vacío en token");
                return null;
            }
            return email;
        } catch (Exception e) {
            logger.debug("Error extrayendo email del token");
            return null;
        }
    }

    /**
     * Extrae el rol del token
     */
    public String obtenerRolDelToken(String token) {
        if (token == null || token.isBlank()) {
            return "USER";
        }

        try {
            Object rol = Jwts.parserBuilder()
                    .setSigningKey(obtenerClaveFirma())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("rol");
            
            if (rol != null && rol instanceof String) {
                String rolStr = rol.toString().trim();
                if (!rolStr.isBlank()) {
                    return rolStr;
                }
            }
            return "USER";
        } catch (Exception e) {
            logger.debug("Error extrayendo rol del token");
            return "USER";
        }
    }

    /**
     * Extrae el ID del usuario del token
     */
    public Integer obtenerIdDelToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            Object id = Jwts.parserBuilder()
                    .setSigningKey(obtenerClaveFirma())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id");
            
            if (id != null) {
                return Integer.parseInt(id.toString());
            }
            return null;
        } catch (NumberFormatException e) {
            logger.warn("ID inválido en token");
            return null;
        } catch (Exception e) {
            logger.debug("Error extrayendo ID del token");
            return null;
        }
    }

    /**
     * Renueva un token JWT de forma segura
     */
    public String renovarToken(String token, Usuario usuario) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token no puede estar vacío");
        }

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }

        if (!validarToken(token)) {
            logger.warn("Intento de renovar token expirado o inválido");
            throw new RuntimeException("Token inválido o expirado - renovación rechazada");
        }

        long tiempoRestante = obtenerTiempoExpiracion(token);
        if (tiempoRestante < BUFFER_RENOVACION_TOKEN) {
            logger.info("Token próximo a expirar pero aún válido para renovación");
        }

        logger.info("Token renovado para usuario: {}", usuario.getCorreo());
        return generarToken(usuario);
    }

    /**
     * Obtiene el tiempo restante hasta la expiración del token
     */
    public long obtenerTiempoExpiracion(String token) {
        if (token == null || token.isBlank()) {
            return -1;
        }

        try {
            Date fechaExpiracion = Jwts.parserBuilder()
                    .setSigningKey(obtenerClaveFirma())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            
            long tiempoRestante = fechaExpiracion.getTime() - System.currentTimeMillis();
            return tiempoRestante > 0 ? tiempoRestante : 0;
        } catch (Exception e) {
            logger.debug("Error obteniendo tiempo de expiración del token");
            return -1;
        }
    }
}
