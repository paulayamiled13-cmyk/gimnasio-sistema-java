package com.gimnasio.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Membresia {
    private int idMembresia;
    private int idSocio;
    private String tipoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private BigDecimal costo;
    private String estado;

    public Membresia(int idMembresia, int idSocio, String tipoMembresia, LocalDate fechaInicio,
                     LocalDate fechaVencimiento, BigDecimal costo, String estado) {
        this.idMembresia = idMembresia;
        this.idSocio = idSocio;
        this.tipoMembresia = tipoMembresia;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.costo = costo;
        this.estado = estado;
    }

    public int getIdMembresia() { return idMembresia; }
    public int getIdSocio() { return idSocio; }
    public String getTipoMembresia() { return tipoMembresia; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public BigDecimal getCosto() { return costo; }
    public String getEstado() { return estado; }
}
