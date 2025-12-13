package com.example.queledoy_backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.queledoy_backend.model.Producto;
import com.example.queledoy_backend.service.ProductoService;

class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProductos() {
        List<Producto> productos = new ArrayList<>();
        Producto prod1 = new Producto();
        prod1.setId(1);
        prod1.setNombre("Laptop");
        prod1.setPrecio(999.99);
        productos.add(prod1);

        when(productoService.getAllProductos()).thenReturn(productos);

        List<Producto> result = productoController.getAllProductos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getNombre());
        verify(productoService, times(1)).getAllProductos();
    }

    @Test
    void testGetProductoById() {
        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Mouse");
        producto.setPrecio(25.99);

        when(productoService.getProductoById(1)).thenReturn(producto);

        Producto result = productoController.getProductoById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Mouse", result.getNombre());
        assertEquals(25.99, result.getPrecio());
        verify(productoService, times(1)).getProductoById(1);
    }

    @Test
    void testSaveProducto() {
        Producto producto = new Producto();
        producto.setNombre("Teclado");
        producto.setPrecio(79.99);
        producto.setStock(50);
        producto.setActivo(true);

        Producto savedProducto = new Producto();
        savedProducto.setId(3);
        savedProducto.setNombre("Teclado");
        savedProducto.setPrecio(79.99);
        savedProducto.setStock(50);

        when(productoService.saveProducto(producto)).thenReturn(savedProducto);

        Producto result = productoController.saveProducto(producto);

        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("Teclado", result.getNombre());
        assertEquals(79.99, result.getPrecio());
        verify(productoService, times(1)).saveProducto(producto);
    }

    @Test
    void testUpdateProducto() {
        Producto existingProducto = new Producto();
        existingProducto.setId(1);
        existingProducto.setNombre("Monitor");
        existingProducto.setPrecio(299.99);
        existingProducto.setStock(10);

        Producto updateData = new Producto();
        updateData.setNombre("Monitor 4K");
        updateData.setPrecio(399.99);
        updateData.setStock(5);

        when(productoService.getProductoById(1)).thenReturn(existingProducto);
        when(productoService.saveProducto(existingProducto)).thenReturn(existingProducto);

        Producto result = productoController.updateProducto(1, updateData);

        assertNotNull(result);
        assertEquals("Monitor 4K", result.getNombre());
        assertEquals(399.99, result.getPrecio());
        verify(productoService, times(1)).getProductoById(1);
        verify(productoService, times(1)).saveProducto(existingProducto);
    }

    @Test
    void testDeleteProducto() {
        doNothing().when(productoService).deleteProducto(1);

        productoController.deleteProducto(1);

        verify(productoService, times(1)).deleteProducto(1);
    }

    @Test
    void testProductoWithNegativePrice() {
        Producto producto = new Producto();
        producto.setNombre("Producto Inválido");
        producto.setPrecio(-50.0);

        assertFalse(producto.getPrecio() > 0, "El precio no puede ser negativo");
    }
}
