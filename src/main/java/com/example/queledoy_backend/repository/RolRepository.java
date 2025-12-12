package com.example.queledoy_backend.repository;

import com.example.queledoy_backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    /**
     * Busca un rol por su nombre (NECESARIO PARA REGISTRO)
     */
    Optional<Rol> findByNombre(String nombre);
    
    /**
     * Verifica si existe un rol con el nombre dado
     */
    boolean existsByNombre(String nombre);
}