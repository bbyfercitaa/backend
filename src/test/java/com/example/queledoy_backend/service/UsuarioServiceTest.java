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

import com.example.queledoy_backend.model.Usuario;
import com.example.queledoy_backend.repository.UsuarioRepository;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Carlos");
        usuario.setCorreo("carlos@example.com");
        usuarios.add(usuario);

        when(usuarioRepository.findAll()).thenReturn(usuarios);
        List<Usuario> result = usuarioService.getAllUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testSaveUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@example.com");
        usuario.setContrasena("pass123");

        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        Usuario result = usuarioService.saveUsuario(usuario);

        assertNotNull(result);
        assertEquals("Ana", result.getNombre());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testDeleteUsuario() {
        doNothing().when(usuarioRepository).deleteById(1);
        usuarioService.deleteUsuario(1);
        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetUsuarioById() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Pedro");
        usuario.setCorreo("pedro@example.com");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        Usuario result = usuarioService.getUsuarioById(1);

        assertNotNull(result);
        assertEquals("Pedro", result.getNombre());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    void testValidateEmail() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        assertTrue(usuario.getCorreo().contains("@"));
    }
}
