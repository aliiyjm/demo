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
@RequestMapping("/api/pedidos") //origen de los endpoints
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    //endpoints 
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
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

    @GetMapping
    public List<Pedido> listarPedidos() {
        return service.listarPedidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable int id) {
        Pedido pedido = service.buscarPorId(id);
        if (pedido == null)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        return ResponseEntity.ok(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        boolean ok = service.eliminarPedido(id);
        if (!ok)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }

    @PostMapping("/despachar")
    public ResponseEntity<?> despachar() {
        Pedido pedido = service.despacharSiguiente();
        if (pedido == null)
            return ResponseEntity.status(409).body("No hay pedidos pendientes");
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/total-recursivo")
    public ResponseEntity<?> totalRecursivo() {
        double total = service.montoTotalRecursivo();
        return ResponseEntity.ok("{\"totalMontoRecursivo\": " + total + "}");
    }

    @PostMapping("/rollback")
    public ResponseEntity<?> rollback() {
        HistorialOperacion op = service.rollback();
        if (op == null)
            return ResponseEntity.status(409).body("No hay operaciones para revertir");
        return ResponseEntity.ok("Rollback de operación: " + op.getTipoOperacion());
    }
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        Map<String, Object> stats = service.obtenerEstadisticas();
    return ResponseEntity.ok(stats);
    }
}
