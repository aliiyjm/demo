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

    private final SinglyLinkedList lista = new SinglyLinkedList(); // lista principal
    private final QueuePedidos cola = new QueuePedidos(); // pedidos pendientes (REGISTRADOS)
    private final Stack historial = new Stack(); // pila de operaciones
    private static int idCounter = 1; // genera ids únicos incrementales

    //REGISTRAR UN NUEVO PEDIDO
    public Pedido registrarPedido(String nombre, String descripcion, double monto) {
        Pedido pedido = new Pedido(idCounter++, nombre, descripcion, monto, "REGISTRADO");

        lista.addLast(pedido);
        cola.enqueue(pedido);
        historial.push(new HistorialOperacion("CREAR", null, pedido));

        return pedido;
    }

    //LISTAR TODOS LOS PEDIDOS
    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        Node actual = lista.getHead();
        while (actual != null) {
            pedidos.add(actual.data);
            actual = actual.next;
        }
        return pedidos;
    }

    //BUSCAR PEDIDO POR ID
    public Pedido buscarPorId(int id) {
        return lista.findById(id);
    }

    //CANCELAR PEDIDO (CAMBIA ESTADO A CANCELADO)
    public boolean cancelarPedido(int id) {
        Pedido pedido = lista.findById(id);
        if (pedido == null) return false;

        // Guardar estado anterior (para rollback)
        Pedido antes = new Pedido(
            pedido.getId(),
            pedido.getNombreCliente(),
            pedido.getDescripcion(),
            pedido.getMonto(),
            pedido.getEstado()
        );

        // Cambiar estado a CANCELADO
        pedido.setEstado("CANCELADO");

        // Quitar de la cola de pendientes si estaba ahí
        cola.reconstruirDesdeLista(lista.toList());

        // Registrar operación en el historial
        historial.push(new HistorialOperacion("CANCELAR", antes, pedido));

        return true;
    }

    //ELIMINAR PEDIDO COMPLETAMENTE
    public boolean eliminarPedido(int id) {
        Pedido p = lista.findById(id);
        if (p == null) return false;

        // Registrar operación para rollback
        historial.push(new HistorialOperacion("ELIMINAR", p, null));

        // Eliminar de la lista
        boolean eliminado = lista.removeById(id);

        // Actualizar cola
        cola.reconstruirDesdeLista(lista.toList());

        return eliminado;
    }

    //DESPACHAR SIGUIENTE PEDIDO (FIFO)
    public Pedido despacharSiguiente() {
        Pedido pedido = cola.dequeue();
        if (pedido == null) return null;

        Pedido antes = new Pedido(
            pedido.getId(),
            pedido.getNombreCliente(),
            pedido.getDescripcion(),
            pedido.getMonto(),
            pedido.getEstado()
        );

        //Cambiar estado a DESPACHADO
        pedido.setEstado("DESPACHADO");

        //Registrar en historial
        historial.push(new HistorialOperacion("DESPACHAR", antes, pedido));

        return pedido;
    }

    //TOTAL RECURSIVO DE MONTOS
    public double montoTotalRecursivo() {
        return sumarRecursivo(lista.getHead());
    }

    private double sumarRecursivo(Node nodo) {
        if (nodo == null) return 0;
        return nodo.data.getMonto() + sumarRecursivo(nodo.next);
    }

    //ROLLBACK (DESHACER ÚLTIMA OPERACIÓN)
    public HistorialOperacion rollback() {
        if (historial.isEmpty()) return null;

        HistorialOperacion op = historial.pop();
        String tipo = op.getTipoOperacion();

        switch (tipo) {
            case "CREAR" -> {
                Pedido creado = op.getPedidoDespues();
                lista.removeById(creado.getId());
                cola.reconstruirDesdeLista(lista.toList());
                System.out.println("Rollback: Pedido eliminado (CREAR revertido)");
            }

            case "CANCELAR" -> {
                Pedido antes = op.getPedidoAntes();
                Pedido actual = lista.findById(antes.getId());
                if (actual != null) {
                    actual.setEstado(antes.getEstado());
                    cola.reconstruirDesdeLista(lista.toList());
                }
                System.out.println("Rollback: Pedido restaurado (CANCELAR revertido)");
            }

            case "DESPACHAR" -> {
                Pedido p = op.getPedidoDespues();
                p.setEstado("REGISTRADO");
                cola.enqueue(p);
                System.out.println("Rollback: Pedido devuelto a REGISTRADO (DESPACHAR revertido)");
            }

            case "ELIMINAR" -> {
                Pedido eliminado = op.getPedidoAntes();
                lista.addLast(eliminado);
                cola.reconstruirDesdeLista(lista.toList());
                System.out.println("Rollback: Pedido restaurado (ELIMINAR revertido)");
            }

            default -> System.out.println("Tipo de operación desconocido: " + tipo);
        }

        return op;
    }

    //ESTADÍSTICAS DE PEDIDOS
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        int totalPedidos = lista.size();
        double totalMonto = montoTotalRecursivo();

        int totalRegistrados = 0;
        int totalDespachados = 0;
        int totalCancelados = 0;

        for (Pedido p : lista.toList()) {
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
