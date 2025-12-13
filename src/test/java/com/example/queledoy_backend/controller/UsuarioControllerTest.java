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

import com.example.queledoy_backend.model.Usuario;
import com.example.queledoy_backend.service.UsuarioService;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Juan");
        usuario.setCorreo("juan@example.com");
        usuarios.add(usuario);

        when(usuarioService.getAllUsuarios()).thenReturn(usuarios);
        List<Usuario> result = usuarioController.getAllUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioService, times(1)).getAllUsuarios();
    }

    @Test
    void testSaveUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Maria");
        usuario.setCorreo("maria@example.com");
        usuario.setContrasena("password123");

        when(usuarioService.saveUsuario(usuario)).thenReturn(usuario);
        Usuario result = usuarioController.saveUsuario(usuario);

        assertNotNull(result);
        assertEquals("Maria", result.getNombre());
        assertEquals("maria@example.com", result.getCorreo());
        verify(usuarioService, times(1)).saveUsuario(usuario);
    }

    @Test
    void testDeleteUsuario() {
        doNothing().when(usuarioService).deleteUsuario(1);
        usuarioController.deleteUsuario(1);
        verify(usuarioService, times(1)).deleteUsuario(1);
    }

    @Test
    void testValidEmailFormat() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("email@example.com");
        assertTrue(usuario.getCorreo().contains("@"));
    }
}
