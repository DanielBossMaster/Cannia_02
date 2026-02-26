package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.Dto.RegistroDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;
import scrum.cannia.service.CodigoVinculacionService;
import scrum.cannia.service.PropietarioService;
import scrum.cannia.service.UsuarioService;
@AllArgsConstructor
@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;
    private final VeterinariaRepository veterinariaRepository;
    private final CodigoVinculacionService codigoVinculacionService;
    private final PropietarioService propietarioService;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("registro", new RegistroDTO());

        return "registro/registrar";
    }

    @PostMapping
    public String registrarUsuario(
            @ModelAttribute("registro") RegistroDTO registroDTO,
                                   Model model, RedirectAttributes redirectAttributes) {

        try {

            usuarioService.registrarUsuario(registroDTO);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Registro exitoso. Ahora puedes iniciar sesión.");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            model.addAttribute("errorUsuario", e.getMessage());
            return "registro/registrar";
        }
    }

    @PostMapping("/propietario")
    @Transactional
    public String registrarPropietario(
            @RequestParam String usuario,
            @RequestParam String contrasena,

            @RequestParam String numDoc,
            @RequestParam String codigo,
            RedirectAttributes redirect
    ) {

        try {

            //  Validar código y obtener propietario
            CodigoVinculacionModel codigoVinculo =
                    codigoVinculacionService.validarCodigo(codigo);

            PropietarioModel propietario = codigoVinculo.getPropietario();

            //  Validar documento
            if (!propietario.getNumDoc().equals(numDoc)) {
                redirect.addFlashAttribute("error", "El documento no corresponde al propietario");
                return "redirect:/registro/propietario";
            }

            //  Validar si ya tiene cuenta
            if (propietario.isCuentaCreada()) {
                redirect.addFlashAttribute("error", "Este propietario ya tiene una cuenta creada");
                return "redirect:/registro/propietario";
            }

            //  Crear usuario
            UsuarioModel usuarioNuevo =
                    usuarioService.crearUsuarioPropietario(usuario, contrasena);

            // Asociar usuario al propietario
            propietarioService.asociarUsuario(propietario, usuarioNuevo);

            // Marcar código como usado
            codigoVinculacionService.marcarComoUsado(codigoVinculo);

            // Éxito → login
            return "redirect:/login";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro/propietario";
        }
    }

    @GetMapping("/propietario")
    public String mostrarRegistroPropietario(Model model) {


        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }

        return "registro/RegistroPropietario";
    }
}
