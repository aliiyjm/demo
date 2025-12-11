package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.HistorialOperacion;
import com.example.demo.model.Pedido;
import com.example.demo.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos") // Base de todos los endpoints
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    //REGISTRAR NUEVO PEDIDO
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        // Validación
        if (pedido.getNombreCliente() == null || pedido.getNombreCliente().isBlank()
                || pedido.getDescripcion() == null || pedido.getDescripcion().isBlank()
                || pedido.getMonto() <= 0)
            return ResponseEntity.badRequest().body("Datos inválidos");

        Pedido nuevo = service.registrarPedido(
                pedido.getNombreCliente(),
                pedido.getDescripcion(),
                pedido.getMonto()
        );
        return ResponseEntity.ok(nuevo);
    }

    //LISTAR TODOS LOS PEDIDOS
    @GetMapping
    public List<Pedido> listarPedidos() {
        return service.listarPedidos();
    }

    //CONSULTAR PEDIDO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable int id) {
        Pedido pedido = service.buscarPorId(id);
        if (pedido == null)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        return ResponseEntity.ok(pedido);
    }

    //CANCELAR PEDIDO (cambia estado a CANCELADO)
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable int id) {
        boolean ok = service.cancelarPedido(id);
        if (!ok)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        return ResponseEntity.ok("Pedido cancelado correctamente");
    }

    //ELIMINAR PEDIDO (borrado total de la lista)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPedido(@PathVariable int id) {
        boolean ok = service.eliminarPedido(id);
        if (!ok)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }

    //DESPACHAR SIGUIENTE PEDIDO (FIFO con la cola)
    @PostMapping("/despachar")
    public ResponseEntity<?> despachar() {
        Pedido pedido = service.despacharSiguiente();
        if (pedido == null)
            return ResponseEntity.status(409).body("No hay pedidos pendientes");
        return ResponseEntity.ok(pedido);
    }

    //TOTAL RECURSIVO (suma de montos usando recursión)
    @GetMapping("/total-recursivo")
    public ResponseEntity<?> totalRecursivo() {
        double total = service.montoTotalRecursivo();
        return ResponseEntity.ok(Map.of("totalMontoRecursivo", total));
    }

    //ROLLBACK (deshacer última operación)
    @PostMapping("/rollback")
    public ResponseEntity<?> rollback() {
        HistorialOperacion op = service.rollback();
        if (op == null)
            return ResponseEntity.status(409).body("No hay operaciones para revertir");
        return ResponseEntity.ok(Map.of(
                "mensaje", "Rollback realizado correctamente",
                "operacionRevertida", op.getTipoOperacion(),
                "pedidoAfectado", op.getPedidoDespues() != null ? op.getPedidoDespues() : op.getPedidoAntes()
        ));
    }

    //ESTADÍSTICAS DE PEDIDOS
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        Map<String, Object> stats = service.obtenerEstadisticas();
        return ResponseEntity.ok(stats);
    }
}
