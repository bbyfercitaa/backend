package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColorTest {

    private Color color;

    @BeforeEach
    void setUp() {
        color = new Color();
    }

    @Test
    void testColorConstructorAndSetters() {
        color.setId(1);
        color.setNombre("Rojo");

        assertEquals(1, color.getId());
        assertEquals("Rojo", color.getNombre());
    }

    @Test
    void testColorAllArgsConstructor() {
        Color c = new Color(2, "Azul");
        
        assertEquals(2, c.getId());
        assertEquals("Azul", c.getNombre());
    }

    @Test
    void testColorNoArgsConstructor() {
        Color c = new Color();
        assertNotNull(c);
    }
}
