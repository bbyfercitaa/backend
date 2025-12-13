package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolTest {

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
    }

    @Test
    void testRolConstructorAndSetters() {
        rol.setId(1);
        rol.setNombre("Admin");

        assertEquals(1, rol.getId());
        assertEquals("Admin", rol.getNombre());
    }

    @Test
    void testRolAllArgsConstructor() {
        Rol r = new Rol(2, "Usuario");
        
        assertEquals(2, r.getId());
        assertEquals("Usuario", r.getNombre());
    }

    @Test
    void testRolNoArgsConstructor() {
        Rol r = new Rol();
        assertNotNull(r);
    }
}
