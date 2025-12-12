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

import com.example.queledoy_backend.model.Genero;
import com.example.queledoy_backend.repository.GeneroRepository;

class GeneroServiceTest {

    @Mock
    private GeneroRepository generoRepository;

    @InjectMocks
    private GeneroService generoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllGeneros() {
        List<Genero> generos = new ArrayList<>();
        Genero genero = new Genero();
        genero.setId(1);
        genero.setNombre("Unisex");
        generos.add(genero);

        when(generoRepository.findAll()).thenReturn(generos);
        List<Genero> result = generoService.getAllGeneros();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(generoRepository, times(1)).findAll();
    }

    @Test
    void testSaveGenero() {
        Genero genero = new Genero();
        genero.setNombre("Niños");

        when(generoRepository.save(genero)).thenReturn(genero);
        Genero result = generoService.saveGenero(genero);

        assertNotNull(result);
        assertEquals("Niños", result.getNombre());
        verify(generoRepository, times(1)).save(genero);
    }

    @Test
    void testDeleteGenero() {
        doNothing().when(generoRepository).deleteById(1);
        generoService.deleteGenero(1);
        verify(generoRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetGeneroById() {
        Genero genero = new Genero();
        genero.setId(1);
        genero.setNombre("Damas");

        when(generoRepository.findById(1)).thenReturn(Optional.of(genero));
        Genero result = generoService.getGeneroById(1);

        assertNotNull(result);
        assertEquals("Damas", result.getNombre());
        verify(generoRepository, times(1)).findById(1);
    }
}
