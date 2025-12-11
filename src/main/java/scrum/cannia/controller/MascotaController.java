package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.model.*;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.PropietarioRepository;

import java.util.List;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;

    @Autowired
    public MascotaController(MascotaRepository mascotaRepository, PropietarioRepository propietarioRepository) {
        this.mascotaRepository = mascotaRepository;
        this.propietarioRepository = propietarioRepository;
    }

    // --------------------------
    // Página principal del propietario
    // --------------------------
    @GetMapping
    public String index(Model model, HttpSession session) {

        // 1. Obtener el usuario en sesión
        UsuarioModel usuarioSesion = (UsuarioModel) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            System.out.println("❌ No hay usuario en sesión. Redirigiendo...");
            return "redirect:/login";
        }

        // 2. Obtener el propietario asociado
        PropietarioModel propietarioSesion = usuarioSesion.getPropietario();
        if (propietarioSesion == null) {
            System.out.println("❌ El usuario NO tiene propietario asociado.");
            return "redirect:/errorRol";
        }

        System.out.println("✔ Propietario autenticado: " + propietarioSesion.getNombrePro());

        // 3. Guardar el propietario en sesión para poder usarlo en la tienda
        session.setAttribute("propietario", propietarioSesion);

        // 4. Traer solo las mascotas de este propietario
        List<MascotaModel> mascotas = mascotaRepository.findByPropietario(propietarioSesion);

        // 5. Agregar atributos al modelo
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("propietario", propietarioSesion);
        model.addAttribute("mascota", new MascotaModel());

        return "propietario/indexPropietario";
    }

    @GetMapping("/misMascotas")
    public String misMascotas(HttpSession session, Model model) {
        // Obtener propietario desde la sesión
        PropietarioModel propietario = (PropietarioModel) session.getAttribute("propietario");
        if (propietario == null) {
            return "redirect:/login";
        }

        // Traer las mascotas del propietario
        List<MascotaModel> listaMascotasDelPropietario = mascotaRepository.findByPropietario(propietario);

        // Pasar al modelo para Thymeleaf
        model.addAttribute("listaMascotas", listaMascotasDelPropietario);

        return "propietario/misMascotas"; // Llama al HTML
    }

    // --------------------------
    // Agrega a la base de datos la mascota generada por el propietario
    // --------------------------
    @PostMapping("/guardar")
    public String guardarMascota(
            @Valid MascotaModel mascota,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos");
            return "redirect:/propietario/index";
        }

        PropietarioModel propietario = (PropietarioModel) session.getAttribute("propietario");

        if (propietario == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró propietario en sesión.");
            return "redirect:/propietario/index";
        }

        System.out.println(">>>> PROPIETARIO ASIGNADO: " + propietario.getId());

        mascota.setPropietario(propietario);

        mascotaRepository.save(mascota);

        redirectAttributes.addFlashAttribute("success", "Mascota registrada correctamente");
        return "redirect:/propietario/index";
    }

}
