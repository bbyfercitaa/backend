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

import com.example.queledoy_backend.model.Color;
import com.example.queledoy_backend.service.ColorService;

class ColorControllerTest {

    @Mock
    private ColorService colorService;

    @InjectMocks
    private ColorController colorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllColores() {
        List<Color> colores = new ArrayList<>();
        Color color = new Color();
        color.setId(1);
        color.setNombre("Rojo");
        colores.add(color);

        when(colorService.getAllColores()).thenReturn(colores);
        List<Color> result = colorController.getAllColores();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(colorService, times(1)).getAllColores();
    }

    @Test
    void testSaveColor() {
        Color color = new Color();
        color.setNombre("Azul");

        when(colorService.saveColor(color)).thenReturn(color);
        Color result = colorController.saveColor(color);

        assertNotNull(result);
        assertEquals("Azul", result.getNombre());
        verify(colorService, times(1)).saveColor(color);
    }

    @Test
    void testDeleteColor() {
        doNothing().when(colorService).deleteColor(1);
        colorController.deleteColor(1);
        verify(colorService, times(1)).deleteColor(1);
    }
}
