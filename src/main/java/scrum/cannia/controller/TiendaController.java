package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.repository.ProductoRepository;

import java.util.List;

@Controller
public class TiendaController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/tienda")
    public String tienda(@RequestParam(value = "q", required = false) String q, Model model) {
        List<ProductoModel> productos;

        if (q != null && !q.isEmpty()) {
            // Buscar por nombre o descripción
            productos = productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(q, q);
        } else {
            // Si no hay búsqueda, traer todos
            productos = productoRepository.findAll();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("q", q); // para mantener el valor en el input
        return "veterinario/TiendaPreview"; // tu template Thymeleaf
    }
}
