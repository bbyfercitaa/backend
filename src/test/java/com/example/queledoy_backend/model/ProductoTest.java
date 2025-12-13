package com.example.queledoy_backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductoTest {

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
    }

    @Test
    void testProductoConstructorAndGettersSetters() {
        producto.setId(1);
        producto.setNombre("Laptop");
        producto.setPrecio(999.99);
        producto.setStock(5);
        producto.setActivo(true);

        assertEquals(1, producto.getId());
        assertEquals("Laptop", producto.getNombre());
        assertEquals(999.99, producto.getPrecio());
        assertEquals(5, producto.getStock());
        assertTrue(producto.getActivo());
    }

    @Test
    void testProductoPrecioPositivo() {
        producto.setPrecio(50.0);
        assertTrue(producto.getPrecio() > 0);
    }

    @Test
    void testProductoStockZero() {
        producto.setStock(0);
        assertEquals(0, producto.getStock());
    }

    @Test
    void testProductoAllArgsConstructor() {
        Producto p = new Producto(1, "Mouse", "url", 25.99, "Descripción", true, false, 10, null, null, null, null, null);
        
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals("Mouse", p.getNombre());
    }

    @Test
    void testProductoDescripcion() {
        producto.setDescripcion("Un excelente producto");
        assertEquals("Un excelente producto", producto.getDescripcion());
    }
}
