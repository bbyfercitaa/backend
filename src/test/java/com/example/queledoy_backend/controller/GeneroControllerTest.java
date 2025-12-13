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

import com.example.queledoy_backend.model.Genero;
import com.example.queledoy_backend.service.GeneroService;

class GeneroControllerTest {

    @Mock
    private GeneroService generoService;

    @InjectMocks
    private GeneroController generoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllGeneros() {
        List<Genero> generos = new ArrayList<>();
        Genero genero = new Genero();
        genero.setId(1);
        genero.setNombre("Masculino");
        generos.add(genero);

        when(generoService.getAllGeneros()).thenReturn(generos);
        List<Genero> result = generoController.getAllGeneros();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(generoService, times(1)).getAllGeneros();
    }

    @Test
    void testSaveGenero() {
        Genero genero = new Genero();
        genero.setNombre("Femenino");

        when(generoService.saveGenero(genero)).thenReturn(genero);
        Genero result = generoController.saveGenero(genero);

        assertNotNull(result);
        assertEquals("Femenino", result.getNombre());
        verify(generoService, times(1)).saveGenero(genero);
    }

    @Test
    void testDeleteGenero() {
        doNothing().when(generoService).deleteGenero(1);
        generoController.deleteGenero(1);
        verify(generoService, times(1)).deleteGenero(1);
    }
}
