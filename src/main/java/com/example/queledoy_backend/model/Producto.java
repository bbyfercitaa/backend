package com.example.queledoy_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private String nombre;

    @Column(name = "urlProducto", length = 1000, nullable = false)
    private String url;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "descripcionProducto", length = 500, nullable = false)
    private String descripcion;

    @Column(name = "disponible", nullable = false)
    private Boolean activo;

    @Column(name = "destacado", nullable = false)
    private Boolean destacado;

    @Column(name = "stock", nullable = false)
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
