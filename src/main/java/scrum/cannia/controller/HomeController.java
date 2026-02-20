package scrum.cannia.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/inicio")
    public String home() {
        return "/login/index";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login/login";
    }

    @GetMapping("/cerrar-sesion")
    public String cerrarSesion() {
        return "redirect:/logout";
    }
}