package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.*;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.ProductoService;
import scrum.cannia.service.ServicioService;

import java.util.Base64;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/inventario/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final ServicioService servicioService;
    private final UsuarioRepository usuarioRepository;

    // ============================================
    //   LISTAR INVENTARIO Y MOSTRAR FORMULARIOS
    // ============================================

    @GetMapping
    public String productos(
            @RequestParam(defaultValue = "0") int pageProductos,
            @RequestParam(defaultValue = "0") int pageServicios,
            @RequestParam(defaultValue = "productos") String vista,
            Model model,
            Authentication authentication) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        // Seguridad: solo veterinarios con veterinaria
        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = usuario.getVeterinario().getVeterinaria();

        // 📦 Paginación SOLO de la veterinaria
        Page<ProductoModel> productosPage =
                productoService.listarPorVeterinaria(
                        veterinaria.getId(),
                        pageProductos, 5
                );

        Page<ServicioModel> serviciosPage =
                servicioService.listarTodosPorVeterinaria(
                        veterinaria.getId(),
                        pageServicios,
                        9
                );

        // 🖼️ Fotos
        productosPage.forEach(p -> {
            if (p.getFoto() != null) {
                p.setFotoBase64(Base64.getEncoder().encodeToString(p.getFoto()));
            }
        });

        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("currentPageProductos", pageProductos);
        model.addAttribute("totalPagesProductos", productosPage.getTotalPages());

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
    //               GUARDAR PRODUCTO
    // ============================================

    @PostMapping("/guardar")
    public String guardarProducto(
            @Validated @ModelAttribute ProductoModel producto,
            @RequestParam("archivoImagen") MultipartFile archivo,
            BindingResult br,
            Authentication authentication,
            Model model
    ) {

        if (br.hasErrors()) {
            model.addAttribute("mensaje", "Por favor corrige los campos.");
            return "Inventario/producto";
        }

        // 1. Usuario autenticado
        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        // 2. Seguridad: solo veterinario con veterinaria
        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = usuario
                .getVeterinario()
                .getVeterinaria();

        // 3. Guardar producto ASOCIADO a la veterinaria
        productoService.guardarProductoVeterinaria(
                producto,
                archivo,
                veterinaria
        );

        return "redirect:/inventario/productos";
    }

    // ============================================
    //              EDITAR PRODUCTO
    // ============================================
    @GetMapping("/editar/{id}")
    public String editarForm(
            @PathVariable int id,
            Model model,
            Authentication authentication) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            return "redirect:/login";
        }

        Integer veterinariaId =
                usuario.getVeterinario().getVeterinaria().getId();

        ProductoModel producto =
                productoService.obtenerProductoVeterinaria(id, veterinariaId);

        if (producto.getFoto() != null) {
            producto.setFotoBase64(
                    Base64.getEncoder().encodeToString(producto.getFoto())
            );
        }

        model.addAttribute("producto", producto);
        return "Inventario/editarProducto";
    }

    // ============================================
    //            ACTUALIZAR PRODUCTO
    // ============================================

    @PostMapping("/actualizar")
    public String actualizarProducto(
            @Validated @ModelAttribute ProductoModel producto,
            @RequestParam("archivoImagen") MultipartFile archivo,
            BindingResult br,
            Authentication authentication,
            Model model) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            return "redirect:/login";
        }

        if (br.hasErrors()) {
            model.addAttribute("mensaje", "Corrige los campos.");
            return "Inventario/editarProducto";
        }

        Integer veterinariaId =
                usuario.getVeterinario().getVeterinaria().getId();

        productoService.actualizar(
                producto,
                archivo);


        return "redirect:/inventario/productos";
    }

}
