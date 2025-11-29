package scrum.cannia.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;

@Controller
public class HomeController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String mostrarIndex() {
        return "login/index";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,
                                @RequestParam String contrasena,
                                HttpSession session,
                                Model model) {

        UsuarioModel u = usuarioRepository.findByUsuarioAndContrasena(usuario, contrasena);

        if (u == null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login/login";
        }

        session.setAttribute("usuario", u);
        session.setAttribute("rol", u.getRol());

        String rol = u.getRol() != null ? u.getRol().trim().toLowerCase() : "";

        switch (rol) {

            case "fundacion":

                if (u.getFundacion() == null) {
                    model.addAttribute("error", "Este usuario no tiene una fundación asociada.");
                    return "login/login";
                }

                // Guardar ID y Nombre de la fundación en sesión
                session.setAttribute("fundacionId", u.getFundacion().getId());
                session.setAttribute("fundacionNombre", u.getFundacion().getNombre());

                return "redirect:/fundaciones/dashboard";
            case "veterinario":
                return "redirect:/veterinario";
            case "propietario":
                return "redirect:/propietario/index";  // tu vista html
            default:
                model.addAttribute("error", "Rol no válido o no asignado");
                session.invalidate();
                return "login/login";
        }
    }
    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}


