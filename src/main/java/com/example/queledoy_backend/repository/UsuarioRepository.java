package com.example.queledoy_backend.repository;

import com.example.queledoy_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca un usuario por su correo electrónico (NECESARIO PARA LOGIN)
     */
    Optional<Usuario> findByCorreo(String correo);
    
    /**
     * Verifica si existe un usuario con el correo dado
     */
    boolean existsByCorreo(String correo);
}