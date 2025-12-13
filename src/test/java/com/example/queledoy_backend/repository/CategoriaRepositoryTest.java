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

import com.example.queledoy_backend.model.Categoria;

class CategoriaRepositoryTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Categoria categoria = new Categoria(1, "Electrónica");
        
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        Optional<Categoria> result = categoriaRepository.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Electrónica", result.get().getNombre());
        verify(categoriaRepository, times(1)).findById(1);
    }

    @Test
    void testFindAll() {
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(new Categoria(1, "Electrónica"));
        categorias.add(new Categoria(2, "Ropa"));

        when(categoriaRepository.findAll()).thenReturn(categorias);

        List<Categoria> result = categoriaRepository.findAll();

        assertEquals(2, result.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testSave() {
        Categoria categoria = new Categoria(1, "Hogar");

        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria result = categoriaRepository.save(categoria);

        assertEquals("Hogar", result.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void testDeleteById() {
        doNothing().when(categoriaRepository).deleteById(1);

        categoriaRepository.deleteById(1);

        verify(categoriaRepository, times(1)).deleteById(1);
    }
}
