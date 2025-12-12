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

import com.example.queledoy_backend.model.Categoria;
import com.example.queledoy_backend.service.CategoriaService;

class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        Categoria cat1 = new Categoria();
        cat1.setId(1);
        cat1.setNombre("Electrónica");
        categorias.add(cat1);

        when(categoriaService.getAllCategorias()).thenReturn(categorias);

        List<Categoria> result = categoriaController.getAllCategorias();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electrónica", result.get(0).getNombre());
        verify(categoriaService, times(1)).getAllCategorias();
    }

    @Test
    void testGetCategoriasById() {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Ropa");

        when(categoriaService.getCategoriasById(1)).thenReturn(categoria);

        Categoria result = categoriaController.getCategoriasById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Ropa", result.getNombre());
        verify(categoriaService, times(1)).getCategoriasById(1);
    }

    @Test
    void testSaveCategorias() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Hogar");

        Categoria savedCategoria = new Categoria();
        savedCategoria.setId(2);
        savedCategoria.setNombre("Hogar");

        when(categoriaService.saveCategorias(categoria)).thenReturn(savedCategoria);

        Categoria result = categoriaController.saveCategorias(categoria);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Hogar", result.getNombre());
        verify(categoriaService, times(1)).saveCategorias(categoria);
    }

    @Test
    void testUpdateCategoria() {
        Categoria existingCategoria = new Categoria();
        existingCategoria.setId(1);
        existingCategoria.setNombre("Electrónica");

        Categoria updateData = new Categoria();
        updateData.setNombre("Tecnología");

        when(categoriaService.getCategoriasById(1)).thenReturn(existingCategoria);
        when(categoriaService.saveCategorias(existingCategoria)).thenReturn(existingCategoria);

        Categoria result = categoriaController.updateCategoria(1, updateData);

        assertNotNull(result);
        assertEquals("Tecnología", result.getNombre());
        verify(categoriaService, times(1)).getCategoriasById(1);
        verify(categoriaService, times(1)).saveCategorias(existingCategoria);
    }

    @Test
    void testDeleteCategorias() {
        doNothing().when(categoriaService).deleteCategorias(1);

        categoriaController.deleteCategorias(1);

        verify(categoriaService, times(1)).deleteCategorias(1);
    }
}
