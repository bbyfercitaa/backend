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


@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final int MIN_SECRET_LENGTH = 64; // Mínimo 64 caracteres para HS256
    private static final long REFRESH_TOKEN_EXPIRY_BUFFER = 3600000; // 1 hora de buffer

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") 
    private long jwtExpiration;

    private SecretKey signingKey;

    /**
     * Inicializa la clave de firma con validación de seguridad
     */
    private SecretKey getSigningKey() {
        if (signingKey == null) {
            validateSecretKey();
            signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
        return signingKey;
    }

    /**
     * Valida que la clave secreta cumple con estándares mínimos de seguridad
     */
    private void validateSecretKey() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalArgumentException(
                "jwt.secret no configurado. Configure 'jwt.secret' en application.properties con una clave de al menos 64 caracteres"
            );
        }
        if (jwtSecret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalArgumentException(
                String.format("jwt.secret muy corta. Mínimo %d caracteres, actual: %d", 
                    MIN_SECRET_LENGTH, jwtSecret.length())
            );
        }
    }

    /**
     * Genera un token JWT para un usuario con validaciones de seguridad
     */
    public String generateToken(Usuario usuario) {
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
        
        // Incluir rol de forma segura
        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            claims.put("rol", usuario.getRol().getNombre());
        } else {
            claims.put("rol", "USER");
        }

        return createToken(claims, usuario.getCorreo());
    }


    /**
     * Crea un token JWT con seguridad en issuance y expiración
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida un token JWT verificando:
     * - Firma (tampering detection)
     * - Expiración
     * - Formato válido
     * - Claims requeridos
     */
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("Intento de validar token null o vacío");
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
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
     * Extrae email del token con validación de seguridad
     */
    public String getUserEmailFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
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
     * Extrae rol del token con validación de seguridad
     */
    public String getRolFromToken(String token) {
        if (token == null || token.isBlank()) {
            return "USER";
        }

        try {
            Object rol = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("rol");
            
            // Validar que el rol sea un string válido
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
     * Extrae ID del usuario del token con validación de seguridad
     */
    public Integer getUserIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            Object id = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
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
     * Renueva un token JWT de forma segura:
     * - Valida que el token actual sea válido
     * - Verifica que no esté expirado
     * - Mantiene los claims del usuario
     * - Emite nuevo con fecha de expiración actualizada
     */
    public String refreshToken(String token, Usuario usuario) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token no puede estar vacío");
        }

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }

        // Validar que el token actual sea válido
        if (!validateToken(token)) {
            logger.warn("Intento de renovar token expirado o inválido");
            throw new RuntimeException("Token inválido o expirado - renovación rechazada");
        }

        // Verificar que queda tiempo para renovar (buffer de 1 hora)
        long remainingTime = getTokenExpirationTime(token);
        if (remainingTime < REFRESH_TOKEN_EXPIRY_BUFFER) {
            logger.info("Token próximo a expirar pero aún válido para renovación");
        }

        logger.info("Token renovado para usuario: {}", usuario.getCorreo());
        return generateToken(usuario);
    }

    /**
     * Obtiene el tiempo restante para la expiración de un token
     */
    public long getTokenExpirationTime(String token) {
        if (token == null || token.isBlank()) {
            return -1;
        }

        try {
            Date expirationDate = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            
            long remainingTime = expirationDate.getTime() - System.currentTimeMillis();
            return remainingTime > 0 ? remainingTime : 0;
        } catch (Exception e) {
            logger.debug("Error obteniendo tiempo de expiración del token");
            return -1;
        }
    }
}
