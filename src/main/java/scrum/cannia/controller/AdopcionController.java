package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.model.MascotaModel;


import scrum.cannia.service.MascotaService;
import scrum.cannia.service.SolicitudAdopcionService;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/adopciones")
@AllArgsConstructor

public class AdopcionController {

    private final MascotaService mascotaService;
    private final SolicitudAdopcionService solicitudService;

    @GetMapping
    public String verMascotas(Model model){

        List<MascotaModel> mascotas = mascotaService.obtenerMascotasDisponibles();

        model.addAttribute("mascotas", mascotas);

        return "adopcion/vistaAdopcion";
    }

    @PostMapping("/solicitar")
    public String solicitarAdopcion(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam Long mascotaId,
            @RequestParam String experiencia,
            @RequestParam String vivienda,
            @RequestParam String motivo,
            @RequestParam String mascotas,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefono
    ){

        solicitudService.crearSolicitud(
                mascotaId,
                experiencia,
                vivienda,
                motivo,
                mascotas,
                nombre,
                email,
                telefono,
                user
        );

        return "redirect:/adopciones";

    }
}
