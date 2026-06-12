package com.gimnasio.model;

public class Socio {
    private int idSocio;
    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String correo;
    private String direccion;
    private String estado;

    public Socio(int idSocio, int idUsuario, String nombres, String apellidos, String dni,
                 String telefono, String correo, String direccion, String estado) {
        this.idSocio = idSocio;
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
    }

    public int getIdSocio() { return idSocio; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDni() { return dni; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getEstado() { return estado; }

    public void setIdSocio(int idSocio) { this.idSocio = idSocio; }
    public void setEstado(String estado) { this.estado = estado; }
}
