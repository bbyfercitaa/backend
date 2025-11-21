package com.example.queledoy_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.queledoy_backend.model.Producto;
import com.example.queledoy_backend.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "API para gestión de productos")
@CrossOrigin(origins = "*") // Agregar CORS aquí también
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista de todos los productos disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        List<Producto> productos = productoService.getAllProductos();
        List<ProductoDTO> productosDTO = productos.stream()
            .map(ProductoDTO::fromProducto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Devuelve un producto específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Integer id) {
        Producto producto = productoService.getProductoById(id);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(ProductoDTO.fromProducto(producto));
    }

    // DTO corregido para exponer correctamente todos los campos
    public static class ProductoDTO {
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

        public static ProductoDTO fromProducto(Producto producto) {
            ProductoDTO dto = new ProductoDTO();
            dto.id = producto.getId();
            dto.nombre_producto = producto.getNombre();
            dto.precio = producto.getPrecio();
            dto.descripcion_producto = producto.getDescripcion();
            dto.activo = producto.getActivo();
            dto.destacado = producto.getDestacado();
            dto.stock = producto.getStock();
            
            // CORRECCIÓN 1: Obtener URL de imagen correctamente
            // Navega por la relación: Producto -> Imagenes -> Imagen -> url
            if (producto.getImagenes() != null && 
                producto.getImagenes().getImagen() != null && 
                producto.getImagenes().getImagen().getUrl() != null) {
                dto.url_imagen = producto.getImagenes().getImagen().getUrl();
            } else {
                // URL de imagen por defecto si no hay imagen
                dto.url_imagen = "https://via.placeholder.com/300x300?text=Sin+Imagen";
            }
            
            // CORRECCIÓN 2: Obtener link de Mercado Libre correctamente
            // El campo 'url' del producto es el link a Mercado Libre
            if (producto.getUrl() != null && !producto.getUrl().isEmpty()) {
                dto.link_mercado = producto.getUrl();
            } else {
                // Link por defecto si no hay URL
                dto.link_mercado = "https://www.mercadolibre.cl";
            }
            
            // CORRECCIÓN 3: Obtener categoría correctamente
            if (producto.getCategorias() != null && 
                producto.getCategorias().getNombre() != null) {
                dto.categoria = producto.getCategorias().getNombre();
            } else {
                dto.categoria = "Sin categoría";
            }
            
            return dto;
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el sistema")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    public ResponseEntity<Producto> saveProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.saveProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
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