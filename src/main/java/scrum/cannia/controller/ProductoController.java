package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.*;
import scrum.cannia.service.ProductoService;
import scrum.cannia.service.ServicioService;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/inventario/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    @Autowired
    private ServicioService servicioService;

    // ============================================
    //   LISTAR INVENTARIO Y MOSTRAR FORMULARIOS
    // ============================================
    @GetMapping
    public String productos(
            @RequestParam(defaultValue = "0") int pageProductos,
            @RequestParam(defaultValue = "0") int pageServicios,
            @RequestParam(defaultValue = "productos") String vista,
            Model model,
            HttpSession session) {

        // 🔐 Sesión
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        // 🏥 Seguridad de relaciones
        VeterinarioModel veterinario = usuario.getVeterinario();
        if (veterinario == null || veterinario.getVeterinaria() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = veterinario.getVeterinaria();


        // 📦 Paginación
        Page<ProductoModel> productosPage =
                productoService.listarPaginado(pageProductos, 5);

        Page<ServicioModel> serviciosPage =
                servicioService.listarPaginado(pageServicios, 9);


        // 🖼️ Fotos a Base64
        for (ProductoModel p : productosPage) {
            if (p.getFoto() != null) {
                p.setFotoBase64(
                        Base64.getEncoder().encodeToString(p.getFoto())
                );
            }
        }

       // Productos
        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("currentPageProductos", pageProductos);
        model.addAttribute("totalPagesProductos", productosPage.getTotalPages());
        // Servicios
        model.addAttribute("servicios", serviciosPage.getContent());
        model.addAttribute("currentPageServicios", pageServicios);
        model.addAttribute("totalPagesServicios", serviciosPage.getTotalPages());

        model.addAttribute("vistaActiva", vista);
        model.addAttribute("producto", new ProductoModel());
        model.addAttribute("servicio", new ServicioModel());


        model.addAttribute("veterinaria", veterinaria);

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
