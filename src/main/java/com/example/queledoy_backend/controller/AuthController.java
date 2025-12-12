package com.example.queledoy_backend.controller;

import com.example.queledoy_backend.model.Usuario;
import com.example.queledoy_backend.service.AuthService;
import com.example.queledoy_backend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@NoArgsConstructor
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService; // ← Servicio transaccional

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // DTOs internos
    public static class LoginRequest {
        public String correo;
        public String contrasena;
    }

    public static class RegisterRequest {
        public String nombre;
        public String correo;
        public String contrasena;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuarioOpt = authService.findByCorreo(loginRequest.correo);
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales inválidas"));
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(loginRequest.contrasena, usuario.getContrasena())) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales inválidas"));
        }

        if (!usuario.getActivo()) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Usuario inactivo"));
        }

        String rolNombre = usuario.getRol() != null ? usuario.getRol().getNombre() : "ROLE_USER";
        String token = jwtUtil.generateToken(usuario.getCorreo(), rolNombre);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
            "id", usuario.getId(),
            "nombre", usuario.getNombre(),
            "correo", usuario.getCorreo(),
            "rol", rolNombre
        ));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (authService.findByCorreo(registerRequest.correo).isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El correo ya está registrado"));
        }

        Usuario usuario = authService.registrarUsuario(
            registerRequest.nombre,
            registerRequest.correo,
            registerRequest.contrasena
        );

        return ResponseEntity.ok(Map.of(
            "message", "Usuario registrado exitosamente",
            "usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "correo", usuario.getCorreo()
            )
        ));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verificar token JWT")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username,
                "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(Map.of("valid", false, "error", "Token inválido"));
        }
    }
}