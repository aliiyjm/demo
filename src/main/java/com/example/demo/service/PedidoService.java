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

    //Registra un nuevo pedido en el sistema y lo agrega a todas las estructuras
    public Pedido registrarPedido(String nombre, String descripcion, double monto) {

        Pedido pedido = new Pedido(idCounter++, nombre, descripcion, monto, "REGISTRADO");

        lista.addLast(pedido);
        cola.enqueue(pedido);
        historial.push(new HistorialOperacion("CREAR", null, pedido));
        return pedido;

    }


    //Devuelve la lista de pedidos
    public List<Pedido> listarPedidos() {
        
        List<Pedido> pedidos = new ArrayList<>();

        Node actual = lista.getHead();
        while (actual != null) {
            pedidos.add(actual.data);
            actual = actual.next;
        }
        return pedidos;
    }

    //Busca un pedido por su id y lo devuelve, si no lo encuentra devuelve null
    public Pedido buscarPorId(int id){ 
        return lista.findById(id);
    }


    //Cancela un pedido por su id, cambia su estado a CANCELADO
    public boolean cancelarPedido(int id) {
        Pedido pedido = lista.findById(id);
        if (pedido == null) return false;
        Pedido antes = new Pedido(pedido.getId(), pedido.getNombreCliente(), pedido.getDescripcion(), pedido.getMonto(), pedido.getEstado());
        pedido.setEstado("CANCELADO");
        historial.push(new HistorialOperacion("CANCELAR", antes, pedido));
        return true;
    }


    //Elimina un pedido por su id de la lista
    public boolean eliminarPedido(int id) {
        Pedido p = lista.findById(id);
        if (p == null) return false;
        historial.push(new HistorialOperacion("ELIMINAR", p, null));
        return lista.removeById(id);
    }


    //Despacha el siguiente pedido en la cola (FIFO)
    public Pedido despacharSiguiente() {

        Pedido pedido = cola.dequeue();
        if (pedido == null) return null;
        Pedido antes = new Pedido(pedido.getId(), pedido.getNombreCliente(), pedido.getDescripcion(), pedido.getMonto(), pedido.getEstado());
        pedido.setEstado("DESPACHADO");
        historial.push(new HistorialOperacion("DESPACHAR", antes, pedido));
        return pedido;
    }


    //Suma el monto del pedido actual y llama al siguiente indice
    public double montoTotalRecursivo() {
        return sumarRecursivo(lista.getHead());
    }

    private double sumarRecursivo(Node nodo) {
        if (nodo == null) return 0;
        return nodo.data.getMonto() + sumarRecursivo(nodo.next);
    }

    // Deshacer la última operacion (hace la operacion contraria)
    public HistorialOperacion rollback() {
        if (historial.isEmpty()) return null;
        return historial.pop();
    }

    //Recorre la lista y obtiene estadisticas de los pedidos (total, montos, estados)
    public Map<String, Object> obtenerEstadisticas() {
    Map<String, Object> stats = new HashMap<>();
    int totalPedidos = lista.size();
    double totalMonto = montoTotalRecursivo(); // usa metodo recursivo
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