package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImagenTest {

    private Imagen imagen;

    @BeforeEach
    void setUp() {
        imagen = new Imagen();
    }

    @Test
    void testImagenConstructorAndSetters() {
        imagen.setId(1);
        imagen.setUrl("https://example.com/image.jpg");

        assertEquals(1, imagen.getId());
        assertEquals("https://example.com/image.jpg", imagen.getUrl());
    }

    @Test
    void testImagenUrlFormat() {
        imagen.setUrl("https://example.com/photo.png");
        assertTrue(imagen.getUrl().startsWith("https://"));
    }

    @Test
    void testImagenAllArgsConstructor() {
        Imagen img = new Imagen(2, "https://example.com/img2.jpg", "Descripción");
        
        assertEquals(2, img.getId());
        assertTrue(img.getUrl().contains("example.com"));
    }

    @Test
    void testImagenNoArgsConstructor() {
        Imagen img = new Imagen();
        assertNotNull(img);
    }
}
