package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scrum.cannia.model.ItemCarrito;
import scrum.cannia.service.CarritoService;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    // ============================================================
    // MOSTRAR ITEMS DEL CARRITO (PARA EL MODAL)
    // ============================================================
    @GetMapping("/carrito/items")
    public String getItems(Model model) {
        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());
        return "Veterinario/Fragmentos/CarritoItems :: items";
    }

    // ============================================================
    // AGREGAR PRODUCTO AL CARRITO
    // ============================================================
    @PostMapping("/carrito/agregar/{id}")
    public ResponseEntity<Void> agregar(@PathVariable Integer id) {
        carritoService.agregar(id);
        return ResponseEntity.ok().build();
    }

    // ============================================================
    // VACIAR CARRITO
    // ============================================================
    @GetMapping("/carrito/vaciar")
    public ResponseEntity<Void> vaciar() {
        carritoService.vaciar();
        return ResponseEntity.ok().build();
    }

    // ============================================================
    // ELIMINAR ITEM DEL CARRITO
    // ============================================================
    @GetMapping("/carrito/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Model model) {
        carritoService.eliminar(id);

        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());


        return "Veterinario/Fragmentos/CarritoItems :: items";
    }

    // ============================================================
    // AUMENTAR CANTIDAD
    // ============================================================
    @PostMapping("/carrito/aumentar/{id}")
    public String aumentar(@PathVariable Integer id, Model model) {
        carritoService.aumentarCantidad(id);

        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());

        return "Veterinario/Fragmentos/CarritoItems :: items";
    }

    // ============================================================
    // DISMINUIR CANTIDAD
    // ============================================================
    @PostMapping("/carrito/disminuir/{id}")
    public String disminuir(@PathVariable Integer id, Model model) {
        carritoService.disminuirCantidad(id);


        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());


        return "Veterinario/Fragmentos/CarritoItems :: items";
    }

    @GetMapping("/carrito/items-json")
    @ResponseBody
    public List<Map<String, Object>> obtenerCarritoJson(HttpSession session) {

        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null) return List.of();

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
