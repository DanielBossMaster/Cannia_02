package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.Dto.RegistroDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;
import scrum.cannia.service.UsuarioService;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;
    private final VeterinariaRepository veterinariaRepository;

    public RegistroController(UsuarioService usuarioService,
                              VeterinariaRepository veterinariaRepository) {
        this.usuarioService = usuarioService;
        this.veterinariaRepository = veterinariaRepository;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("registro", new RegistroDTO());
        model.addAttribute("veterinarias", veterinariaRepository.findAll());
        return "registro/registrar";
    }

    @PostMapping
    public String registrarUsuario(@ModelAttribute("registro") RegistroDTO registroDTO,
                                   Model model, RedirectAttributes redirectAttributes) {

        try {

            usuarioService.registrarUsuario(registroDTO);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Registro exitoso. Ahora puedes iniciar sesión.");

            return "login/login";

        } catch (IllegalArgumentException e) {

            model.addAttribute("errorUsuario", e.getMessage());
            return "registro/registrar";
        }
    }
}
