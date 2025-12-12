package com.example.queledoy_backend.service;

import com.example.queledoy_backend.model.Rol;
import com.example.queledoy_backend.model.Usuario;
import com.example.queledoy_backend.repository.RolRepository;
import com.example.queledoy_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional
    public Usuario registrarUsuario(String nombre, String correo, String contrasena) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(passwordEncoder.encode(contrasena));
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(new Date(System.currentTimeMillis()));

        Rol rol = rolRepository.findByNombre("ROLE_USER")
            .orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombre("ROLE_USER");
                return rolRepository.save(nuevoRol);
            });
        nuevoUsuario.setRol(rol);

        return usuarioRepository.save(nuevoUsuario);
    }
}