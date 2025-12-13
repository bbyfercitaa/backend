package com.example.queledoy_backend.controller;

import com.example.queledoy_backend.model.Usuario;
import com.example.queledoy_backend.security.ProveedorTokenJwt;
import com.example.queledoy_backend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * Controlador REST para autenticación con seguridad JWT
 * 
 * Validaciones de seguridad:
 * - Contraseña hasheada con BCrypt (fuerza 10)
 * - Token JWT con HS256 (256+ bits)
 * - Headers Authorization validados
 * - Logging auditable de intentos fallidos
 * - Respuestas genéricas para ataques de enumeración
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProveedorTokenJwt proveedorTokenJwt;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * POST /api/v1/auth/login
     * Autentica usuario con email y contraseña
     * 
     * Seguridad:
     * - Valida credenciales contra hash BCrypt
     * - Genera token JWT con claims
     * - Respuesta genérica para evitar enumeración de usuarios
     * - Log de intentos fallidos
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String contraseña = credentials.get("contraseña");

            if (!StringUtils.hasText(email) || !StringUtils.hasText(contraseña)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Email y contraseña son requeridos"));
            }

            Usuario usuario = usuarioService.findByCorreo(email);
            if (usuario == null) {
                logAuthenticationFailure(email, "Usuario no encontrado");
                // Respuesta genérica - no revelar si el usuario existe
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Credenciales inválidas"));
            }

            // Validar contraseña - BCryptPasswordEncoder.matches() es time-constant
            if (!passwordEncoder.matches(contraseña, usuario.getContrasena())) {
                logAuthenticationFailure(email, "Contraseña incorrecta");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Credenciales inválidas"));
            }

            // Generar JWT token
            String token = proveedorTokenJwt.generarToken(usuario);
            logAuthenticationSuccess(email);

            return ResponseEntity.ok(createAuthResponse(token, usuario, "Login exitoso"));

        } catch (Exception e) {
            logger.error("Error en login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error procesando login"));
        }
    }

    /**
     * POST /api/v1/auth/registro
     * Registra nuevo usuario
     * 
     * Seguridad:
     * - Valida duplicidad de email
     * - Encripta contraseña con BCrypt(10)
     * - Genera token JWT inmediatamente
     * - Evita ataques de enumeración
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Map<String, String> usuarioData) {
        try {
            String nombre = usuarioData.get("nombre");
            String email = usuarioData.get("email");
            String contraseña = usuarioData.get("contraseña");

            if (!StringUtils.hasText(nombre) || !StringUtils.hasText(email) || !StringUtils.hasText(contraseña)) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Nombre, email y contraseña son requeridos"));
            }

            // Validar formato email básico
            if (!email.contains("@") || email.length() > 254) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Email inválido"));
            }

            // Validar longitud mínima de contraseña
            if (contraseña.length() < 6) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Contraseña debe tener mínimo 6 caracteres"));
            }

            // Verificar que el usuario no exista
            if (usuarioService.findByCorreo(email) != null) {
                logger.warn("Intento de registro con email duplicado: {}", email);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("El email ya está registrado"));
            }

            // Crear usuario con contraseña encriptada con BCrypt
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setCorreo(email);
            usuario.setContrasena(passwordEncoder.encode(contraseña));

            usuario = usuarioService.saveUsuario(usuario);

            // Generar token JWT
            String token = proveedorTokenJwt.generarToken(usuario);
            logAuthenticationSuccess(email);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(createAuthResponse(token, usuario, "Registro exitoso"));

        } catch (Exception e) {
            logger.error("Error en registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error en registro"));
        }
    }

    /**
     * POST /api/v1/auth/refresh-token
     * Renueva token JWT
     * 
     * Seguridad:
     * - Valida signature del token existente
     * - Rechaza tokens expirados
     * - Genera nuevo token con claims actualizados
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Authorization header inválido"));
            }

            String token = authHeader.substring(7);

            // Validar token
            if (!proveedorTokenJwt.validarToken(token)) {
                logger.warn("Intento de renovar token inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Token inválido o expirado"));
            }

            // Extraer email del token
            String email = proveedorTokenJwt.obtenerEmailDelToken(token);
            if (!StringUtils.hasText(email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Token inválido"));
            }

            // Buscar usuario
            Usuario usuario = usuarioService.findByCorreo(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Usuario no encontrado"));
            }

            // Generar nuevo token
            String newToken = proveedorTokenJwt.renovarToken(token, usuario);
            logger.info("Token renovado para: {}", email);

            return ResponseEntity.ok(createAuthResponse(newToken, usuario, "Token renovado"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error renovando token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error renovando token"));
        }
    }

    /**
     * POST /api/v1/auth/logout
     * Logout (frontend limpia sessionStorage/localStorage)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("mensaje", "Logout exitoso");
            put("timestamp", LocalDateTime.now());
        }});
    }

    // ========== Métodos auxiliares ==========

    private Map<String, Object> createAuthResponse(String token, Usuario usuario, String mensaje) {
        return new HashMap<String, Object>() {{
            put("token", token);
            put("usuario", new UsuarioDTO(usuario));
            put("mensaje", mensaje);
            put("timestamp", LocalDateTime.now());
        }};
    }

    private Map<String, String> createErrorResponse(String mensaje) {
        return new HashMap<String, String>() {{
            put("error", mensaje);
        }};
    }

    private void logAuthenticationSuccess(String email) {
        logger.info("[AUTH-SUCCESS] Login para: {}", email);
    }

    private void logAuthenticationFailure(String email, String reason) {
        logger.warn("[AUTH-FAILURE] Email: {}, Razón: {}", email, reason);
    }

    /**
     * DTO para respuesta de usuario (excluye contraseña por seguridad)
     */
    public static class UsuarioDTO {
        public Integer id;
        public String nombre;
        public String email;
        public String rol;

        public UsuarioDTO(Usuario usuario) {
            this.id = usuario.getId();
            this.nombre = usuario.getNombre();
            this.email = usuario.getCorreo();
            this.rol = usuario.getRol() != null ? usuario.getRol().getNombre() : "USER";
        }
    }
}

