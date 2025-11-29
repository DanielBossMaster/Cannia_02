package scrum.cannia.controller;

import scrum.cannia.model.ProductoModel;
import scrum.cannia.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.obtenerTodosProductos());
        return "inventario/productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("producto", new ProductoModel());
        return "inventario/productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute ProductoModel producto) {
        productoService.guardarProducto(producto);
        return "redirect:/inventario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Integer id, Model model) {
        productoService.obtenerProductoPorId(id).ifPresent(producto ->
                model.addAttribute("producto", producto));
        return "inventario/productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        productoService.eliminarProductoLogicamente(id);
        return "redirect:/inventario";
    }
}