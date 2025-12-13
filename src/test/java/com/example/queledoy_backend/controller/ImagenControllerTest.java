package com.example.queledoy_backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.queledoy_backend.service.ImagenService;

@WebMvcTest(ImagenController.class)
class ImagenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImagenService imagenService;

    @Test
    @WithMockUser
    void testGetAllImagens() throws Exception {
        when(imagenService.getAllImagenes()).thenReturn(java.util.List.of());
        mockMvc.perform(get("/api/v1/imagenes")).andExpect(status().isOk());
        verify(imagenService, times(1)).getAllImagenes();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveImagen() throws Exception {
        mockMvc.perform(post("/api/v1/imagenes")
            .contentType("application/json")
            .content("{}")
            .with(csrf())).andExpect(status().isOk());
        verify(imagenService, times(1)).saveImagen(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteImagen() throws Exception {
        doNothing().when(imagenService).deleteImagen(1);
        mockMvc.perform(delete("/api/v1/imagenes/1").with(csrf())).andExpect(status().isOk());
        verify(imagenService, times(1)).deleteImagen(1);
    }
}
