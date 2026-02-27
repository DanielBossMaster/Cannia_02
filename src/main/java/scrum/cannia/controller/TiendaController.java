package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import scrum.cannia.Dto.ProductoBusquedaDto;
import scrum.cannia.model.*;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.CategoriaService;
import scrum.cannia.service.ProductoService;
import scrum.cannia.service.ServicioService;

import java.util.List;

@Controller
@RequestMapping("/tienda")
@AllArgsConstructor
public class TiendaController {

    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ServicioService servicioService;

    // ============================================
    //      TIENDA PARA PROPIETARIO (LECTURA)
    // ============================================
    @GetMapping("/propietario/tienda")
    public String tiendaPropietario(
            Authentication authentication,
            Model model,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "idCategoria", required = false) Long idCategoria
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        PropietarioModel propietario = usuario.getPropietario();
        if (propietario == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria =
                propietario.getVeterinario().getVeterinaria();

        if (veterinaria == null) {
            model.addAttribute("error",
                    "La veterinaria aún no ha sido configurada por el veterinario.");
            model.addAttribute("productos", List.of());
            model.addAttribute("categorias", List.of());
            return "tienda/Tienda";
        }

        List<ProductoBusquedaDto> productos =
                productoService.obtenerProductosActivosFiltradosPorVeterinaria(
                        veterinaria,
                        q,
                        idCategoria
                );

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("veterinaria", veterinaria);
        model.addAttribute("consulta", q);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        model.addAttribute("direccion", propietario.getDireccionPro());

        return "tienda/Tienda";
    }

    // ============================================
    //        SERVICIOS (VISTA PROPIETARIO)
    // ============================================
    @GetMapping("/propietario/servicios")
    public String serviciosPropietario(
            Authentication authentication,
            Model model
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("Usuario no autenticado"));

        // Seguridad: solo propietarios
        if (usuario.getPropietario() == null) {
            return "redirect:/login";
        }

        PropietarioModel propietario = usuario.getPropietario();

        VeterinariaModel veterinaria =
                propietario.getVeterinario().getVeterinaria();

        if (veterinaria == null) {
            model.addAttribute("error",
                    "La veterinaria aún no ha sido configurada por el veterinario.");
            model.addAttribute("productos", List.of());
            model.addAttribute("categorias", List.of());
            return "tienda/Servicios";
        }


        List<ServicioModel> servicios =
                servicioService.listarActivosPorVeterinaria(
                        veterinaria.getId()
                );

        model.addAttribute("servicios", servicios);
        model.addAttribute("veterinaria", veterinaria);
        model.addAttribute("direccion", propietario.getDireccionPro());

        return "tienda/Servicios"; // 👈 vista del propietario
    }

    // ============================================
    //             TIENDA DE VETERINARIO
    // ============================================

    @GetMapping("/veterinario/tienda/preview")
    public String tiendaPreview(
            Authentication authentication,
            Model model,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "idCategoria", required = false) Long idCategoria
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        // Seguridad: solo veterinarios
        if (usuario.getVeterinario() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = usuario.getVeterinario().getVeterinaria();
        model.addAttribute("veterinaria", veterinaria);

        try {
            boolean esBusqueda = (q != null && !q.trim().isEmpty()) || (idCategoria != null);
            String queryParaWS = (q == null || q.trim().isEmpty()) ? null : q.trim();

            List<ProductoBusquedaDto> resultados =
                    productoService.obtenerProductosActivosFiltradosPorVeterinaria(
                            veterinaria,
                            queryParaWS,
                            idCategoria
                    );

            model.addAttribute("productos", resultados);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("consulta", q);
            model.addAttribute("categoriaSeleccionada", idCategoria);
            model.addAttribute("esBusqueda", esBusqueda);

        } catch (Exception e) {
            model.addAttribute("errorBusqueda", "Error al procesar la solicitud de productos.");
            model.addAttribute("productos", List.of());
            model.addAttribute("direccion", "Vista previa (sin dirección)");
        }

        return "tienda/TiendaPreview";
    }


    // ============================================
    //         SERVICIOS (VISTA VETERINARIO)
    // ============================================

    @GetMapping("/veterinario/servicios/preview")
    public String serviciosPreview(
            Authentication authentication,
            Model model
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        // Seguridad: solo veterinarios
        if (usuario.getVeterinario() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = usuario.getVeterinario().getVeterinaria();

        model.addAttribute("servicios", servicioService.listarActivosPorVeterinaria(veterinaria.getId()));
        model.addAttribute("veterinaria", veterinaria);

        return "tienda/ServiciosPreview";
    }
}