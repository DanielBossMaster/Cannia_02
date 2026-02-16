package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.UsuarioModel;

import scrum.cannia.model.VeterinariaModel;
import scrum.cannia.repository.ServicioRepository;

import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.ServicioService;

@AllArgsConstructor
@Controller
@RequestMapping("/inventario/servicios")
public class ServicioController {

    private final ServicioService servicioService;
    private final UsuarioRepository usuarioRepository;

    // ============================================
    //           GUARDAR SERVICIO
    // ============================================

    @PostMapping("/guardar")
    public String guardarServicio(
            @Validated @ModelAttribute ServicioModel servicio,
            BindingResult br,
            Authentication authentication,
            Model model
    ) {

        if (br.hasErrors()) {
            model.addAttribute("mensaje", "Error en los datos del servicio");
            return "Inventario/Servicio";
        }

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria =
                usuario.getVeterinario().getVeterinaria();

        servicioService.guardarServicioVeterinaria(servicio, veterinaria);

        return "redirect:/inventario/productos";
    }

    // ============================================
    //           EDITAR SERVICIO
    // ============================================

    @GetMapping("/editar/{id}")
    public String editarServicio(
            @PathVariable Integer id,
            Authentication authentication,
            Model model
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        Integer veterinariaId =
                usuario.getVeterinario().getVeterinaria().getId();

        ServicioModel servicio =
                servicioService.obtenerServicioVeterinaria(id, veterinariaId);

        model.addAttribute("servicio", servicio);
        return "Inventario/EditarServicio";
    }

    @PostMapping("/actualizar")
    public String actualizarServicio(
            @ModelAttribute ServicioModel servicio,
            Authentication authentication
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        Integer veterinariaId =
                usuario.getVeterinario().getVeterinaria().getId();

        servicioService.actualizarServicioVeterinaria(servicio, veterinariaId);

        return "redirect:/inventario/servicios";
    }
}