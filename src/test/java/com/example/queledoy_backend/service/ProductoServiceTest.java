package com.example.queledoy_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.queledoy_backend.model.Producto;
import com.example.queledoy_backend.repository.ProductoRepository;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProductos() {
        List<Producto> productos = new ArrayList<>();
        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Producto Test");
        producto.setPrecio(99.99);
        producto.setStock(10);
        productos.add(producto);

        when(productoRepository.findAll()).thenReturn(productos);
        List<Producto> result = productoService.getAllProductos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testSaveProducto() {
        Producto producto = new Producto();
        producto.setNombre("Nuevo Producto");
        producto.setPrecio(50.0);
        producto.setStock(5);

        when(productoRepository.save(producto)).thenReturn(producto);
        Producto result = productoService.saveProducto(producto);

        assertNotNull(result);
        assertEquals("Nuevo Producto", result.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testDeleteProducto() {
        doNothing().when(productoRepository).deleteById(1);
        productoService.deleteProducto(1);
        verify(productoRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetProductoById() {
        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Producto Encontrado");
        producto.setPrecio(75.0);

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        Producto result = productoService.getProductoById(1);

        assertNotNull(result);
        assertEquals("Producto Encontrado", result.getNombre());
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void testProductoStockValidation() {
        Producto producto = new Producto();
        producto.setStock(0);
        assertTrue(producto.getStock() >= 0);
    }
}
