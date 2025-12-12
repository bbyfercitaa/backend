package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListaTest {

    private Lista lista;

    @BeforeEach
    void setUp() {
        lista = new Lista();
    }

    @Test
    void testListaConstructorAndSetters() {
        lista.setId(1);
        lista.setNombre("Mi Lista");

        assertEquals(1, lista.getId());
        assertEquals("Mi Lista", lista.getNombre());
    }

    @Test
    void testListaAllArgsConstructor() {
        Lista l = new Lista(2, "Compras", "Lista de compras");
        
        assertNotNull(l);
        assertEquals("Compras", l.getNombre());
    }

    @Test
    void testListaNoArgsConstructor() {
        Lista l = new Lista();
        assertNotNull(l);
    }
}
