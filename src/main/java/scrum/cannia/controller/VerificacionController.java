package scrum.cannia.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.service.UsuarioService;

@Controller
@RequestMapping("/verificacion")
public class VerificacionController {

    private final UsuarioService usuarioService;

    public VerificacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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
