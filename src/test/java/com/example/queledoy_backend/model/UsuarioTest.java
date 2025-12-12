package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Test
    void testUsuarioConstructorAndSetters() {
        usuario.setId(1);
        usuario.setNombre("Juan");
        usuario.setCorreo("juan@example.com");
        usuario.setContrasena("password123");
        usuario.setActivo(true);

        assertEquals(1, usuario.getId());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("juan@example.com", usuario.getCorreo());
        assertTrue(usuario.getActivo());
    }

    @Test
    void testEmailFormat() {
        usuario.setCorreo("test@example.com");
        assertTrue(usuario.getCorreo().contains("@"));
    }

    @Test
    void testContrasenaNotNull() {
        usuario.setContrasena("secure123");
        assertNotNull(usuario.getContrasena());
    }

    @Test
    void testNombreNotEmpty() {
        usuario.setNombre("Maria");
        assertFalse(usuario.getNombre().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        Usuario u = new Usuario(1, "Pedro", "pedro@example.com", "pass", true, null, null, null);
        
        assertNotNull(u);
        assertEquals("Pedro", u.getNombre());
    }
}
