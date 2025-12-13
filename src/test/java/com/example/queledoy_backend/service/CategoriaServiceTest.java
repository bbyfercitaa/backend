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

import com.example.queledoy_backend.model.Categoria;
import com.example.queledoy_backend.repository.CategoriaRepository;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        Categoria cat = new Categoria();
        cat.setId(1);
        cat.setNombre("Electrónica");
        categorias.add(cat);

        when(categoriaRepository.findAll()).thenReturn(categorias);

        List<Categoria> result = categoriaService.getAllCategorias();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testSaveCategorias() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Ropa");

        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria result = categoriaService.saveCategorias(categoria);

        assertNotNull(result);
        assertEquals("Ropa", result.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void testDeleteCategorias() {
        doNothing().when(categoriaRepository).deleteById(1);

        categoriaService.deleteCategorias(1);

        verify(categoriaRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetCategoriasById() {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Hogar");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        Categoria result = categoriaService.getCategoriasById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(categoriaRepository, times(1)).findById(1);
    }
}
