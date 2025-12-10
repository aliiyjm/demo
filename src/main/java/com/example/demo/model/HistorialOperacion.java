package com.example.demo.model;


//Registro para el historial de operaciones
public class HistorialOperacion {
    private String tipoOperacion;
    private Pedido pedidoAntes;
    private Pedido pedidoDespues;

    public HistorialOperacion(String tipoOperacion, Pedido pedidoAntes, Pedido pedidoDespues) {
        this.tipoOperacion = tipoOperacion;
        this.pedidoAntes = pedidoAntes;
        this.pedidoDespues = pedidoDespues;
    }

    public String getTipoOperacion() { return tipoOperacion; }
    public Pedido getPedidoAntes() { return pedidoAntes; }
    public Pedido getPedidoDespues() { return pedidoDespues; }
}
