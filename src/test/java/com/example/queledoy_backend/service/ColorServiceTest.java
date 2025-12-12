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

import com.example.queledoy_backend.model.Color;
import com.example.queledoy_backend.repository.ColorRepository;

class ColorServiceTest {

    @Mock
    private ColorRepository colorRepository;

    @InjectMocks
    private ColorService colorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllColores() {
        List<Color> colores = new ArrayList<>();
        Color color = new Color();
        color.setId(1);
        color.setNombre("Verde");
        colores.add(color);

        when(colorRepository.findAll()).thenReturn(colores);
        List<Color> result = colorService.getAllColores();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(colorRepository, times(1)).findAll();
    }

    @Test
    void testSaveColor() {
        Color color = new Color();
        color.setNombre("Amarillo");

        when(colorRepository.save(color)).thenReturn(color);
        Color result = colorService.saveColor(color);

        assertNotNull(result);
        assertEquals("Amarillo", result.getNombre());
        verify(colorRepository, times(1)).save(color);
    }

    @Test
    void testDeleteColor() {
        doNothing().when(colorRepository).deleteById(1);
        colorService.deleteColor(1);
        verify(colorRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetColorById() {
        Color color = new Color();
        color.setId(1);
        color.setNombre("Negro");

        when(colorRepository.findById(1)).thenReturn(Optional.of(color));
        Color result = colorService.getColorById(1);

        assertNotNull(result);
        assertEquals("Negro", result.getNombre());
        verify(colorRepository, times(1)).findById(1);
    }
}
