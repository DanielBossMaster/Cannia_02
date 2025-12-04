package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // ============================================
    //   LISTAR INVENTARIO Y MOSTRAR FORMULARIOS
    // ============================================
    @GetMapping
    public String productos(Model model) {

        model.addAttribute("producto", new ProductoModel());
        model.addAttribute("servicio", new ServicioModel());
        model.addAttribute("productos", productoService.listarTodos());

        return "veterinario/inventario";


    }


    // ============================================
    //         GUARDAR PRODUCTO
    // ============================================
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute ProductoModel producto,
                                  @RequestParam("archivoImagen") MultipartFile archivo) {

        productoService.guardar(producto, archivo);

        return "redirect:/productos";
        // <-- vuelve a la misma vista
    }
}
