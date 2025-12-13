package com.example.queledoy_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.queledoy_backend.model.Categoria;
import com.example.queledoy_backend.service.CategoriaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping
    public List<Categoria> getAllCategorias() {
        return categoriaService.getAllCategorias();
    }

    @GetMapping("/{id}")
    public Categoria getCategoriasById(@PathVariable Integer id) {
        return categoriaService.getCategoriasById(id);
    }

    @PostMapping
    public Categoria saveCategorias(@RequestBody Categoria categoria) {
        return categoriaService.saveCategorias(categoria);
    }

    @PutMapping("/{id}")
    public Categoria updateCategoria(@PathVariable Integer id, @RequestBody Categoria categoria) {
        Categoria existingCategoria = categoriaService.getCategoriasById(id);
        if (existingCategoria != null) {
            existingCategoria.setNombre(categoria.getNombre());
            return categoriaService.saveCategorias(existingCategoria);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteCategorias(@PathVariable Integer id) {
        categoriaService.deleteCategorias(id);
    }
}
