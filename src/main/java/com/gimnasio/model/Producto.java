package com.gimnasio.model;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private int idUsuario;
    private String nombreProducto;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private String estado;

    public Producto(int idProducto, int idUsuario, String nombreProducto, String descripcion,
                    BigDecimal precio, int stock, String estado) {
        this.idProducto = idProducto;
        this.idUsuario = idUsuario;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
    }

    public int getIdProducto() { return idProducto; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombreProducto() { return nombreProducto; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getEstado() { return estado; }
}
