package com.example.queledoy_backend.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.queledoy_backend.model.Producto;

class ProductoRepositoryTest {

    @Mock
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Laptop");
        
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        Optional<Producto> result = productoRepository.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getNombre());
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void testFindAll() {
        List<Producto> productos = new ArrayList<>();
        Producto p1 = new Producto();
        p1.setId(1);
        p1.setNombre("Mouse");
        productos.add(p1);

        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> result = productoRepository.findAll();

        assertEquals(1, result.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testSave() {
        Producto producto = new Producto();
        producto.setNombre("Teclado");

        when(productoRepository.save(producto)).thenReturn(producto);

        Producto result = productoRepository.save(producto);

        assertEquals("Teclado", result.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testDeleteById() {
        doNothing().when(productoRepository).deleteById(1);

        productoRepository.deleteById(1);

        verify(productoRepository, times(1)).deleteById(1);
    }
}
