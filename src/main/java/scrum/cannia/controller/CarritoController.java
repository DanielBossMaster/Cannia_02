package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.service.CarritoService;
import scrum.cannia.service.ProductoService;

import java.util.Map;

@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;

    @PostMapping("/agregar/{id}")
    public Map<String, Object> agregar(@PathVariable Integer id, HttpSession session) {
        ProductoModel producto = productoService.buscarPorId(id);
        carritoService.agregarProducto(session, producto);

        return Map.of(
                "mensaje", "Producto agregado",
                "total", carritoService.getTotal(session)
        );
    }

    @DeleteMapping("/eliminar/{id}")
    public Map<String, Object> eliminar(@PathVariable Integer id, HttpSession session) {
        carritoService.eliminarProducto(session, id);

        return Map.of(
                "mensaje", "Producto eliminado",
                "total", carritoService.getTotal(session)
        );
    }

    @GetMapping("/listar")
    public Map<String, Object> listar(HttpSession session) {
        return Map.of(
                "items", carritoService.listarItems(session),
                "total", carritoService.getTotal(session)
        );
    }

    @PostMapping("/vaciar")
    public Map<String, Object> vaciar(HttpSession session) {
        carritoService.vaciarCarrito(session);
        return Map.of("mensaje", "Carrito vaciado");
    }
}
