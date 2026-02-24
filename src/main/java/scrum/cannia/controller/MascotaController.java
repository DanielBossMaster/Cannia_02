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
import scrum.cannia.service.MascotaServiceCreator;
import scrum.cannia.service.creator.MascotaCreator;
import scrum.cannia.service.creator.MascotaPropietarioCreator;

import java.util.List;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaServiceCreator mascotaServiceCreator;
    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;

    @Autowired
    public MascotaController(
            MascotaRepository mascotaRepository,
            PropietarioRepository propietarioRepository,
            MascotaServiceCreator mascotaServiceCreator) {

        this.mascotaRepository = mascotaRepository;
        this.propietarioRepository = propietarioRepository;
        this.mascotaServiceCreator = mascotaServiceCreator;
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

        return "Propietario/index";
    }

    @PostMapping("/guardar")
    public String guardarMascota(
            @RequestParam String nomMascota,
            @RequestParam String especie,
            @RequestParam String raza,
            @RequestParam String color,
            @RequestParam Genero genero,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        PropietarioModel propietario =
                (PropietarioModel) session.getAttribute("propietario");

        if (propietario == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "No se encontró propietario en sesión.");
            return "redirect:/login";
        }

        //  TEMPLATE METHOD
        mascotaServiceCreator.crearDesdePropietario(
                nomMascota,
                especie,
                raza,
                color,
                genero,
                propietario
        );

        redirectAttributes.addFlashAttribute(
                "success", "Mascota registrada correctamente");

        return "redirect:/propietario/index";
    }


    @PostMapping("/editar")
    public String editarMascota(
            @RequestParam Long id,
            @RequestParam String nomMascota,
            @RequestParam String especie,
            @RequestParam String raza,
            @RequestParam String color,
            @RequestParam Genero genero,
            HttpSession session) {

        MascotaModel mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        mascota.setNomMascota(nomMascota);
        mascota.setEspecie(especie);
        mascota.setRaza(raza);
        mascota.setColor(color);
        mascota.setGenero(genero);

        mascotaRepository.save(mascota);

        return "redirect:/propietario/index";
    }


}
