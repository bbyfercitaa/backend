package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoriaTest {

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
    }

    @Test
    void testConstructorAndSetters() {
        categoria.setId(1);
        categoria.setNombre("Electrónica");

        assertEquals(1, categoria.getId());
        assertEquals("Electrónica", categoria.getNombre());
    }

    @Test
    void testAllArgsConstructor() {
        Categoria cat = new Categoria(2, "Ropa");
        
        assertEquals(2, cat.getId());
        assertEquals("Ropa", cat.getNombre());
    }

    @Test
    void testNoArgsConstructor() {
        Categoria cat = new Categoria();
        assertNotNull(cat);
    }

    @Test
    void testNombreNotNull() {
        categoria.setNombre("Hogar");
        assertNotNull(categoria.getNombre());
    }
}
