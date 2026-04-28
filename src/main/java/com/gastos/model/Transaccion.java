package com.gastos.model;

/**
 * Representa una transacción (ingreso o gasto) de un usuario.
 */
public class Transaccion {
    private int id;
    private int usuarioId;
    private String username; 
    private String tipo; 
    private String categoria;
    private double cantidad;
    private String fecha;

    public Transaccion() {
    }

    public Transaccion(int id, int usuarioId, String username, String tipo, String categoria, double cantidad, String fecha) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.username = username;
        this.tipo = tipo;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", username='" + username + '\'' +
                ", tipo='" + tipo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", cantidad=" + cantidad +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
