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

import com.example.queledoy_backend.model.Imagen;
import com.example.queledoy_backend.repository.ImagenRepository;

class ImagenServiceTest {

    @Mock
    private ImagenRepository imagenRepository;

    @InjectMocks
    private ImagenService imagenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllImagens() {
        List<Imagen> imagens = new ArrayList<>();
        Imagen imagen = new Imagen();
        imagen.setId(1);
        imagen.setUrl("https://example.com/img1.jpg");
        imagens.add(imagen);

        when(imagenRepository.findAll()).thenReturn(imagens);
        List<Imagen> result = imagenService.getAllImagenes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(imagenRepository, times(1)).findAll();
    }

    @Test
    void testSaveImagen() {
        Imagen imagen = new Imagen();
        imagen.setUrl("https://example.com/img2.jpg");

        when(imagenRepository.save(imagen)).thenReturn(imagen);
        Imagen result = imagenService.saveImagen(imagen);

        assertNotNull(result);
        assertTrue(result.getUrl().startsWith("https://"));
        verify(imagenRepository, times(1)).save(imagen);
    }

    @Test
    void testDeleteImagen() {
        doNothing().when(imagenRepository).deleteById(1);
        imagenService.deleteImagen(1);
        verify(imagenRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetImagenById() {
        Imagen imagen = new Imagen();
        imagen.setId(1);
        imagen.setUrl("https://example.com/img3.jpg");

        when(imagenRepository.findById(1)).thenReturn(Optional.of(imagen));
        Imagen result = imagenService.getImagenById(1);

        assertNotNull(result);
        assertTrue(result.getUrl().contains("example.com"));
        verify(imagenRepository, times(1)).findById(1);
    }
}
