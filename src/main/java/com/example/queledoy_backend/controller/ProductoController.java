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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.queledoy_backend.model.Producto;
import com.example.queledoy_backend.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "API para gestión de productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    @Operation(summary = "Obtener todos los productos")
    public ResponseEntity<List<ProductoSimpleDTO>> getAllProductos() {
        List<Producto> productos = productoService.getAllProductos();
        List<ProductoSimpleDTO> productosDTO = productos.stream()
            .map(ProductoSimpleDTO::fromProducto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoSimpleDTO> getProductoById(@PathVariable Integer id) {
        Producto producto = productoService.getProductoById(id);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(ProductoSimpleDTO.fromProducto(producto));
    }

    // DTO SIMPLE que maneja campos null
    public static class ProductoSimpleDTO {
        public Integer id;
        public String nombre_producto;
        public Double precio;
        public String descripcion_producto;
        public String url_imagen;
        public String categoria;
        public String link_mercado;
        public Boolean activo;
        public Boolean destacado;
        public Integer stock;

        public static ProductoSimpleDTO fromProducto(Producto producto) {
            ProductoSimpleDTO dto = new ProductoSimpleDTO();
            dto.id = producto.getId();
            dto.nombre_producto = producto.getNombre();
            dto.precio = producto.getPrecio();
            dto.descripcion_producto = producto.getDescripcion();
            dto.activo = producto.getActivo();
            dto.destacado = producto.getDestacado();
            dto.stock = producto.getStock();
            
            // El campo URL del producto ES el link de Mercado Libre
            dto.link_mercado = producto.getUrl() != null ? producto.getUrl() : "#";
            
            // Intentar obtener imagen de la relación, si no existe usar placeholder
            try {
                if (producto.getImagenes() != null && 
                    producto.getImagenes().getImagen() != null && 
                    producto.getImagenes().getImagen().getUrl() != null) {
                    dto.url_imagen = producto.getImagenes().getImagen().getUrl();
                } else {
                    // Imagen por defecto
                    dto.url_imagen = "https://via.placeholder.com/400x400/4DB6AC/FFFFFF?text=" + 
                                    (producto.getNombre() != null ? producto.getNombre().replaceAll(" ", "+") : "Producto");
                }
            } catch (Exception e) {
                dto.url_imagen = "https://via.placeholder.com/400x400/4DB6AC/FFFFFF?text=Producto";
            }
            
            // Intentar obtener categoría
            try {
                if (producto.getCategorias() != null && 
                    producto.getCategorias().getNombre() != null) {
                    dto.categoria = producto.getCategorias().getNombre();
                } else {
                    dto.categoria = "General";
                }
            } catch (Exception e) {
                dto.categoria = "General";
            }
            
            return dto;
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto")
    public ResponseEntity<Producto> saveProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.saveProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (Exception e) {
            // Log del error
            System.err.println("Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        Producto existingProducto = productoService.getProductoById(id);
        if (existingProducto != null) {
            existingProducto.setNombre(producto.getNombre());
            existingProducto.setUrl(producto.getUrl());
            existingProducto.setPrecio(producto.getPrecio());
            existingProducto.setDescripcion(producto.getDescripcion());
            existingProducto.setActivo(producto.getActivo());
            existingProducto.setDestacado(producto.getDestacado());
            existingProducto.setStock(producto.getStock());
            existingProducto.setCategorias(producto.getCategorias());
            existingProducto.setLista(producto.getLista());
            existingProducto.setColores(producto.getColores());
            existingProducto.setGeneros(producto.getGeneros());
            existingProducto.setImagenes(producto.getImagenes());
            return ResponseEntity.ok(productoService.saveProducto(existingProducto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
