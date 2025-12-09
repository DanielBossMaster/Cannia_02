package scrum.cannia.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.service.InventarioService;
import scrum.cannia.service.ProductoService;
import scrum.cannia.service.ServicioService;

import java.util.Map;
import java.util.List;

//Controlador principal para el módulo de Inventario
// Gestiona la navegación entre inventario, productos, servicios y gráficos

@Controller
@RequestMapping("/inventario")
public class InventarioController {
    //
    @Autowired
    private ProductoService productoService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private InventarioService inventarioService;

    /**
     * Página principal del inventario - muestra productos y servicios
     */
    @GetMapping
    public String mostrarInventario(Model model, HttpSession session) {

        // 1. Verificar si hay usuario en sesión
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";  // Seguridad
        }

        // 2. Cargar datos que necesita la vista del inventario
        model.addAttribute("productos", productoService.obtenerProductosActivos());
        model.addAttribute("servicios", servicioService.listarTodos());

        // 3. Agregar objetos vacíos si se usan formularios dentro del inventario
        model.addAttribute("producto", new ProductoModel());
        model.addAttribute("servicio", new ServicioModel());

        // 4. Marcar página actual (útil para resaltar menú)
        model.addAttribute("paginaActual", "inventario");

        // 5. Devolver la vista
        return "Inventario/Index";
    }


}


