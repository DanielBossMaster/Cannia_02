package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.service.UsuarioService;

@AllArgsConstructor
@Controller
@RequestMapping("/verificacion")
public class VerificacionController {

    private final UsuarioService usuarioService;



    @GetMapping
    public String pantallaVerificacion() {
        return "verificacion/verificacion";
    }

    @PostMapping("/solicitar")
    public String solicitarActivacion(Authentication authentication) {

        String username = authentication.getName();

        usuarioService.enviarSolicitudPorUsername(username);

        return "redirect:/verificacion/proceso";
    }

    @GetMapping("/proceso")
    public String proceso() {
        return "verificacion/proceso";
    }
}
