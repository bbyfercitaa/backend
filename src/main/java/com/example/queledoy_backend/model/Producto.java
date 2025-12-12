package com.example.queledoy_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombreProducto", length = 100, nullable = false)
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Column(name = "urlProducto", length = 1000, nullable = false)
    @NotBlank(message = "La URL del producto es obligatoria")
    private String url;

    @Column(name = "precio", nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede exceder 999999.99")
    private Double precio;

    @Column(name = "descripcionProducto", length = 500, nullable = false)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @Column(name = "disponible", nullable = false)
    @NotNull(message = "El estado de disponibilidad es obligatorio")
    private Boolean activo;

    @Column(name = "destacado", nullable = false)
    @NotNull(message = "El estado de destacado es obligatorio")
    private Boolean destacado;

    @Column(name = "stock", nullable = false)
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 99999, message = "El stock no puede exceder 99999")
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "codigo_categoria")
    private Categorias categorias;

    @ManyToOne
    @JoinColumn(name = "codigo_lista")
    private Lista lista;

    @ManyToOne
    @JoinColumn(name = "codigo_color")
    private Colores colores;

    @ManyToOne
    @JoinColumn(name = "codigo_genero")
    private Generos generos;

    @ManyToOne
    @JoinColumn(name = "codigo_imagen")
    private Imagenes imagenes;
}