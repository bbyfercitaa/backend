package com.example.queledoy_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.queledoy_backend.model.Usuario;
import com.example.queledoy_backend.repository.UsuarioRepository;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Login sencillo (sin JWT real). Devuelve user + token ficticio.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");
        String contrasena = payload.get("contrasena");

        if (correo == null || contrasena == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Correo y contrasena son requeridos"));
        }

        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("success", false, "error", "Credenciales incorrectas"));
        }

        Usuario user = opt.get();
        // Atención: si guardas passwords encriptadas, reemplaza esta comparación
        if (user.getContrasena() == null || !user.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(401).body(Map.of("success", false, "error", "Credenciales incorrectas"));
        }

        // Generar token simple (UUID). Para producción usa JWT y validación.
        String token = UUID.randomUUID().toString();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", user);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
