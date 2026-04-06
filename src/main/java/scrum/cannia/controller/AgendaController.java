package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.AgendaService;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/agenda")
@AllArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final UsuarioRepository usuarioRepository; // 👈 nuevo


    @PostMapping("/confirmar")
    public String confirmarAgenda(

            @RequestParam Integer servicioId,
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime hora,
            Authentication authentication,
            RedirectAttributes redirect

    ) {

        String username =
                authentication.getName();

        UsuarioModel usuario =
                usuarioRepository
                        .findByUsuario(username)
                        .orElseThrow();

        if (usuario.getPropietario() == null) {

            return "redirect:/login";
        }

        try {

            agendaService.agendarServicio(

                    servicioId,
                    fecha,
                    hora,
                    usuario.getPropietario()

            );

            redirect.addFlashAttribute(

                    "success",
                    "Cita agendada correctamente"

            );

        } catch (RuntimeException e) {

            if (e.getMessage().equals("HORARIO_OCUPADO")) {

                redirect.addFlashAttribute(

                        "error",
                        "Ese horario ya está ocupado"

                );

            } else {

                redirect.addFlashAttribute(

                        "error",
                        "Error al agendar"

                );

            }

        }

        return "redirect:/tienda/propietario/tienda";
    }



    @GetMapping("/citas/modal")
    public String cargarCitasModal(

            Authentication authentication,
            Model model

    ) {

        String username =
                authentication.getName();

        UsuarioModel usuario =
                usuarioRepository
                        .findByUsuario(username)
                        .orElseThrow();

        if (usuario.getPropietario() == null) {

            return "/Fragmentos/CitasVacio :: contenido";
        }

        model.addAttribute(

                "citas",

                agendaService.citasPorPropietario(

                        usuario.getPropietario()

                )

        );

        return "/Fragmentos/CitasModal :: contenido";
    }

}