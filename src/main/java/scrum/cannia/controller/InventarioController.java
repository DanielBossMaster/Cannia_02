package scrum.cannia.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

        // Obtener el usuario en sesión
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";  // Seguridad
        }
        // Cargar productos activos y todos los servicios para mostrar en la vista
        model.addAttribute("productos", productoService.obtenerProductosActivos());
        model.addAttribute("servicios", servicioService.listarTodos());
        model.addAttribute("paginaActual", "inventario");
        return "Inventario/Index";
    }

//    /**
//     * Página de gráficos del inventario - datos precalculados
//     */
//    @GetMapping("/graficos")
//    public String mostrarGraficos(Model model) {
//        // Obtener datos para los gráficos
//        Map<String, Object> datosGraficos = inventarioService.obtenerDatosGraficos();
//
//        // Calcular métricas para los gráficos
//        List<InventarioModel> stockCritico = (List<InventarioModel>) datosGraficos.get("stockCritico");
//        List<InventarioModel> stockNormal = (List<InventarioModel>) datosGraficos.get("stockNormal");
//        List<InventarioModel> stockAlto = (List<InventarioModel>) datosGraficos.get("stockAlto");
//
//        int totalProductos = stockCritico.size() + stockNormal.size() + stockAlto.size();
//
//        // Calcular porcentajes para gráficos
//        double porcentajeCritico = totalProductos > 0 ? (stockCritico.size() * 100.0) / totalProductos : 0;
//        double porcentajeNormal = totalProductos > 0 ? (stockNormal.size() * 100.0) / totalProductos : 0;
//        double porcentajeAlto = totalProductos > 0 ? (stockAlto.size() * 100.0) / totalProductos : 0;
//
//        // Agregar datos al modelo
//        model.addAttribute("stockCritico", stockCritico);
//        model.addAttribute("stockNormal", stockNormal);
//        model.addAttribute("stockAlto", stockAlto);
//        model.addAttribute("totalProductos", totalProductos);
//        model.addAttribute("porcentajeCritico", porcentajeCritico);
//        model.addAttribute("porcentajeNormal", porcentajeNormal);
//        model.addAttribute("porcentajeAlto", porcentajeAlto);
//        model.addAttribute("paginaActual", "graficos");
//
//        return "inventario/graficos";
//    }
//
//    //Redirección a la página de productos
//    @GetMapping("/productos")
//    public String redirigirAProductos() {
//        return "redirect:/productos";
//    }
//edireccion a la pagina de servicios
//
//    @GetMapping("/servicios")
//    public String redirigirAServicios() {
//        return "redirect:/servicios";
    }



