package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.service.ProductoService;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/inventario/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // ============================================
    //   LISTAR INVENTARIO Y MOSTRAR FORMULARIOS
    // ============================================
    @GetMapping
    public String productos(Model model, HttpSession session) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        // Nuevo producto para el formulario
        model.addAttribute("producto", new ProductoModel());

        // Lista de productos
        List<ProductoModel> lista = productoService.listarTodos();

        // 🔥 Convertimos foto (byte[]) → Base64 ANTES DE ENVIARLA A LA VISTA
        for (ProductoModel p : lista) {
            if (p.getFoto() != null) {
                String base64 = Base64.getEncoder().encodeToString(p.getFoto());
                p.setFotoBase64(base64);
            }
        }

        model.addAttribute("productos", lista);

        return "Inventario/producto";
    }

    // ============================================
    //         GUARDAR PRODUCTO
    // ============================================
    @PostMapping("/guardar")
    public String guardarProducto(@Validated @ModelAttribute ProductoModel producto,
                                  @RequestParam("archivoImagen") MultipartFile archivo,
                                  BindingResult br,
                                  HttpSession session,
                                  Model model) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        if (br.hasErrors()) {
            model.addAttribute("mensaje", "Por favor corrige los campos marcados.");
            return "Inventario/producto";
        }

        productoService.guardar(producto, archivo);

        return "redirect:/inventario/productos";
    }

    // Abrir página de edición
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, HttpSession session) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        ProductoModel producto = productoService.buscarPorId(id);
        if (producto == null) {
            return "redirect:/inventario/productos"; // o mostrar mensaje de error
        }

        model.addAttribute("producto", producto);
        return "Inventario/editarProducto";
    }

    // Actualizar (ya tenías /actualizar, asegúrate que coincide)
    @PostMapping("/actualizar")
    public String actualizarProducto(@Validated @ModelAttribute ProductoModel producto,
                                     @RequestParam("archivoImagen") MultipartFile archivo,
                                     BindingResult br,
                                     HttpSession session,
                                     Model model) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        if (br.hasErrors()) {
            model.addAttribute("mensaje", "Corrige los campos.");
            return "Inventario/editarProducto";
        }

        try {
            productoService.actualizar(producto, archivo);
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error al actualizar: " + e.getMessage());
            return "Inventario/editarProducto";
        }

        return "redirect:/inventario/productos";
    }



}
