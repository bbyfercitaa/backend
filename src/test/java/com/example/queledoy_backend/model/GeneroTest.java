package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneroTest {

    private Genero genero;

    @BeforeEach
    void setUp() {
        genero = new Genero();
    }

    @Test
    void testGeneroConstructorAndSetters() {
        genero.setId(1);
        genero.setNombre("Masculino");

        assertEquals(1, genero.getId());
        assertEquals("Masculino", genero.getNombre());
    }

    @Test
    void testGeneroAllArgsConstructor() {
        Genero g = new Genero(2, "Femenino");
        
        assertEquals(2, g.getId());
        assertEquals("Femenino", g.getNombre());
    }

    @Test
    void testGeneroNoArgsConstructor() {
        Genero g = new Genero();
        assertNotNull(g);
    }
}
