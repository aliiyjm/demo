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

    //Recibe los datos en JSON, los valida, llama al servicio, lo agrega a la lista y devuelve el pedido creado como JSON
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

    //Llama al metodo listar y devuelve la lista de pedidos como JSON
    @GetMapping
    public List<Pedido> listarPedidos() {
        return service.listarPedidos();
    }

    //Obtiene el id, si no lo encuentra devuelve 404 
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable int id) {
        Pedido pedido = service.buscarPorId(id);
        if (pedido == null)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        return ResponseEntity.ok(pedido);
    }

    //Obtiene el id, llama a cancelarPedido, si no lo encuentra devuelve 404
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        boolean ok = service.eliminarPedido(id);
        if (!ok)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }

    //FIFO, cambia su estado a DESPACHADO
    @PostMapping("/despachar")
    public ResponseEntity<?> despachar() {
        Pedido pedido = service.despacharSiguiente();
        if (pedido == null)
            return ResponseEntity.status(409).body("No hay pedidos pendientes");
        return ResponseEntity.ok(pedido);
    }

    //Suma el monto de todos los pedidos
    @GetMapping("/total-recursivo")
    public ResponseEntity<?> totalRecursivo() {
        double total = service.montoTotalRecursivo();
        return ResponseEntity.ok("{\"totalMontoRecursivo\": " + total + "}");
    }

    //Quita la ultima operacion realizada
    @PostMapping("/rollback")
    public ResponseEntity<?> rollback() {
        HistorialOperacion op = service.rollback();
        if (op == null)
            return ResponseEntity.status(409).body("No hay operaciones para revertir");
        return ResponseEntity.ok("Rollback de operación: " + op.getTipoOperacion());
    }
    
    //Obtiene estadisticas de los pedidos
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        Map<String, Object> stats = service.obtenerEstadisticas();
    return ResponseEntity.ok(stats);
    }
}
