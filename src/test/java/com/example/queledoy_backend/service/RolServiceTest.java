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

import com.example.queledoy_backend.model.Rol;
import com.example.queledoy_backend.repository.RolRepository;

class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {
        List<Rol> roles = new ArrayList<>();
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("Vendedor");
        roles.add(rol);

        when(rolRepository.findAll()).thenReturn(roles);
        List<Rol> result = rolService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void testSaveRol() {
        Rol rol = new Rol();
        rol.setNombre("Moderador");

        when(rolRepository.save(rol)).thenReturn(rol);
        Rol result = rolService.saveRol(rol);

        assertNotNull(result);
        assertEquals("Moderador", result.getNombre());
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void testDeleteRol() {
        doNothing().when(rolRepository).deleteById(1);
        rolService.deleteRol(1);
        verify(rolRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetRolById() {
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("Gerente");

        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        Rol result = rolService.getRolById(1);

        assertNotNull(result);
        assertEquals("Gerente", result.getNombre());
        verify(rolRepository, times(1)).findById(1);
    }
}
