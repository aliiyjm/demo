package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.datastructure.Node;
import com.example.demo.datastructure.QueuePedidos;
import com.example.demo.datastructure.SinglyLinkedList;
import com.example.demo.datastructure.Stack;
import com.example.demo.model.HistorialOperacion;
import com.example.demo.model.Pedido;

@Service
public class PedidoService {
    private final SinglyLinkedList lista = new SinglyLinkedList();
    private final QueuePedidos cola = new QueuePedidos();
    private final Stack historial = new Stack();
    private int idCounter = 1;

    public Pedido registrarPedido(String nombre, String descripcion, double monto) {
        Pedido pedido = new Pedido(idCounter++, nombre, descripcion, monto, "REGISTRADO");
        lista.add(pedido);
        cola.enqueue(pedido);
        historial.push(new HistorialOperacion("CREAR", null, pedido));
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        Node actual = lista.getHead();
        while (actual != null) {
            pedidos.add(actual.data);
            actual = actual.next;
        }
        return pedidos;
    }

    public Pedido buscarPorId(int id) { return lista.findById(id); }

    public boolean cancelarPedido(int id) {
        Pedido pedido = lista.findById(id);
        if (pedido == null) return false;
        Pedido antes = new Pedido(pedido.getId(), pedido.getNombreCliente(), pedido.getDescripcion(), pedido.getMonto(), pedido.getEstado());
        pedido.setEstado("CANCELADO");
        historial.push(new HistorialOperacion("CANCELAR", antes, pedido));
        return true;
    }

    public boolean eliminarPedido(int id) {
        Pedido p = lista.findById(id);
        if (p == null) return false;
        historial.push(new HistorialOperacion("ELIMINAR", p, null));
        return lista.removeById(id);
    }

    public Pedido despacharSiguiente() {
        Pedido pedido = cola.dequeue();
        if (pedido == null) return null;
        Pedido antes = new Pedido(pedido.getId(), pedido.getNombreCliente(), pedido.getDescripcion(), pedido.getMonto(), pedido.getEstado());
        pedido.setEstado("DESPACHADO");
        historial.push(new HistorialOperacion("DESPACHAR", antes, pedido));
        return pedido;
    }

    public double montoTotalRecursivo() {
        return sumarRecursivo(lista.getHead());
    }

    private double sumarRecursivo(Node nodo) {
        if (nodo == null) return 0;
        return nodo.data.getMonto() + sumarRecursivo(nodo.next);
    }

    public HistorialOperacion rollback() {
        if (historial.isEmpty()) return null;
        return historial.pop();
    }
    public Map<String, Object> obtenerEstadisticas() {
    Map<String, Object> stats = new HashMap<>();
    int totalPedidos = lista.size();
    double totalMonto = montoTotalRecursivo(); // usa método recursivo
    int totalRegistrados = 0;
    int totalDespachados = 0;
    int totalCancelados = 0;

    // Recorre pedidos en la lista para contar estados
    for (Pedido p : lista.toList()) { // usa el metodo toList()
        if (p.getEstado() != null) {
            switch (p.getEstado()) {
                case "REGISTRADO" -> totalRegistrados++;
                case "DESPACHADO" -> totalDespachados++;
                case "CANCELADO" -> totalCancelados++;
            }
        }
    }

    stats.put("totalPedidos", totalPedidos);
    stats.put("totalMonto", totalMonto);
    stats.put("totalRegistrados", totalRegistrados);
    stats.put("totalDespachados", totalDespachados);
    stats.put("totalCancelados", totalCancelados);

    return stats;
    }
}
