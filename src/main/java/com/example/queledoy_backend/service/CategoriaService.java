package com.example.queledoy_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.queledoy_backend.model.Categoria;
import com.example.queledoy_backend.repository.CategoriaRepository;
import java.util.List;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria getCategoriasById(Integer id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public Categoria saveCategorias(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void deleteCategorias(Integer id) {
        categoriaRepository.deleteById(id);
    }
}