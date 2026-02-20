package scrum.cannia.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ItemCarrito;
import scrum.cannia.service.CarritoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;

        // =========================
        // FRAGMENTO DEL CARRITO
        // =========================
        @GetMapping("/items")
        public String items(Authentication authentication, Model model) {

            String username = authentication.getName();

            model.addAttribute("items", carritoService.getItems(username));
            model.addAttribute("total", carritoService.getTotal(username));

            return "Fragmentos/CarritoItems :: items";
        }

        // =========================
        // AGREGAR
        // =========================
        @PostMapping("/agregar/{id}")
        @ResponseBody
        public void agregar(@PathVariable Integer id, Authentication authentication) {
            carritoService.agregar(authentication.getName(), id);
        }

        // =========================
        // AUMENTAR
        // =========================
        @PostMapping("/aumentar/{id}")
        public String aumentar(@PathVariable Integer id,
                               Authentication authentication,
                               Model model) {

            String username = authentication.getName();
            carritoService.aumentarCantidad(username, id);

            model.addAttribute("items", carritoService.getItems(username));
            model.addAttribute("total", carritoService.getTotal(username));

            return "Fragmentos/CarritoItems :: items";
        }

        // =========================
        // DISMINUIR
        // =========================
        @PostMapping("/disminuir/{id}")
        public String disminuir(@PathVariable Integer id,
                                Authentication authentication,
                                Model model) {

            String username = authentication.getName();
            carritoService.disminuirCantidad(username, id);

            model.addAttribute("items", carritoService.getItems(username));
            model.addAttribute("total", carritoService.getTotal(username));

            return "Fragmentos/CarritoItems :: items";
        }

        // =========================
        // ELIMINAR
        // =========================
        @GetMapping("/eliminar/{id}")
        public String eliminar(@PathVariable Integer id,
                               Authentication authentication,
                               Model model) {

            String username = authentication.getName();
            carritoService.eliminar(username, id);

            model.addAttribute("items", carritoService.getItems(username));
            model.addAttribute("total", carritoService.getTotal(username));

            return "Fragmentos/CarritoItems :: items";
        }

        // =========================
        // VACIAR
        // =========================
        @GetMapping("/vaciar")
        @ResponseBody
        public void vaciar(Authentication authentication) {
            carritoService.limpiar(authentication.getName());
        }


    // ============================================================
    // ITEMS JSON (AJAX / FETCH)
    // ============================================================
    @GetMapping("/items-json")
    @ResponseBody
    public List<Map<String, Object>> obtenerCarritoJson(
            Authentication authentication
    ) {

        List<ItemCarrito> carrito =
                carritoService.getItems(authentication.getName());

        return carrito.stream()
                .map(item -> {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("idProducto", item.getProducto().getId());
                    datos.put("nombre", item.getProducto().getNombre());
                    datos.put("descripcion", item.getProducto().getDescripcion());
                    datos.put("precio", item.getProducto().getValor());
                    datos.put("cantidad", item.getCantidad());
                    datos.put("subtotal", item.getSubtotal());
                    return datos;
                })
                .collect(Collectors.toList());
    }
}