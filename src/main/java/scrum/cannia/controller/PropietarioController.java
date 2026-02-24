package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.*;
import scrum.cannia.repository.HistoriaClinicaRepository;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.MascotaService;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/propietario")
public class PropietarioController {

    private final UsuarioRepository usuarioRepository;
    private final MascotaService mascotaService;
    private final HistoriaClinicaRepository historiaRepository;

    // ============================================
    //           DASHBOARD PROPIETARIO
    // ============================================

    @GetMapping("/index")
    public String indexPropietario(

            Authentication authentication,
            Model model

    ) {
        PropietarioModel propietario = obtenerPropietario(authentication);


        List<MascotaModel> mascotas =
                mascotaService.listarPorPropietario(propietario);


        model.addAttribute("mascotas", mascotas);
        model.addAttribute("mascota", new MascotaModel());
        model.addAttribute("propietario", propietario);

        return "propietario/index";
    }

    // ============================================
    //         REGISTRAR NUEVA MASCOTA
    // ============================================

    @PostMapping("/guardar")
    public String guardarMascota(
            @ModelAttribute MascotaModel mascota,
            Authentication authentication
    ) {

        PropietarioModel propietario = obtenerPropietario(authentication);
        mascotaService.registrarMascota(mascota, propietario);

        return "redirect:/propietario/index";
    }

    // ============================================
    //     HISTORIA CLÍNICA (DETALLE MASCOTA)
    // ============================================

    @GetMapping("/mascota/{id}/historia")
    public String verHistoriaClinica(
            @PathVariable Long id,
            Authentication authentication,
            Model model
    ) {

        PropietarioModel propietario = obtenerPropietario(authentication);

        MascotaModel mascota =
                mascotaService.obtenerMascotaPropietario(id, propietario);

        model.addAttribute("mascota", mascota);
        model.addAttribute(
                "historias",
                historiaRepository.findByMascotaIdOrderByFechaHoraDesc(id)
        );

        return "propietario/index";
    }

    // ======================================================
    //     MetoDO PRIVADO AUXILIAR ( CLAVE DE SEGURIDAD)
    // ======================================================

    private PropietarioModel obtenerPropietario(Authentication authentication) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("Usuario no encontrado"));

        if (usuario.getPropietario() == null) {
            throw new IllegalStateException("El usuario no es propietario");
        }

        return usuario.getPropietario();
    }

}