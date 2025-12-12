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

import com.example.queledoy_backend.model.Rol;
import com.example.queledoy_backend.service.RolService;

class RolControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController rolController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {
        List<Rol> roles = new ArrayList<>();
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("Admin");
        roles.add(rol);

        when(rolService.getAllRoles()).thenReturn(roles);
        List<Rol> result = rolController.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rolService, times(1)).getAllRoles();
    }

    @Test
    void testSaveRol() {
        Rol rol = new Rol();
        rol.setNombre("Usuario");

        when(rolService.saveRol(rol)).thenReturn(rol);
        Rol result = rolController.saveRol(rol);

        assertNotNull(result);
        assertEquals("Usuario", result.getNombre());
        verify(rolService, times(1)).saveRol(rol);
    }

    @Test
    void testDeleteRol() {
        doNothing().when(rolService).deleteRol(1);
        rolController.deleteRol(1);
        verify(rolService, times(1)).deleteRol(1);
    }
}
