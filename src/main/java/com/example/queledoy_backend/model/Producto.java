package com.example.queledoy_backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @Column(name = "nombreProducto", length = 50, nullable = false)
    private String nombre;

    @Column(name = "urlProducto", nullable = false)
    private String url;

    @Column(name = "precio", length = 8, nullable = false)
    private Double precio;

    @Column(name = "descripcionProducto", length = 100, nullable = false)
    private String descripcion;

    @Column(name = "disponible", length = 10, nullable = false)
    private Boolean activo;

    @Column(name = "destacado", length = 10, nullable = false)
    private Boolean destacado;

    @Column(name = "stock", length = 100, nullable = false)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "codigo_categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "codigo_lista")
    private Lista lista;

    @ManyToOne
    @JoinColumn(name = "codigo_color")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "codigo_genero")
    private Genero genero;

    @ManyToOne
    @JoinColumn(name = "codigo_imagen")
    private Imagen imagen;
}