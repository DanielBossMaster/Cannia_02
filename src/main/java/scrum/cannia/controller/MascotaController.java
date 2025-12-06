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
import scrum.cannia.repository.InventarioRepository;
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



    @Autowired
    private InventarioRepository inventarioRepository;

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

    // --------------------------
    // Página de la tienda del propietario
    // --------------------------
    @GetMapping("/tienda")
    public String tiendaPropietario(HttpSession session, Model model) {

        // 1. Obtener el propietario desde la sesión
        PropietarioModel propietario = (PropietarioModel) session.getAttribute("propietario");
        if (propietario == null) {
            return "redirect:/login";
        }

        // 2. Tomar su veterinaria
        VeterinariaModel veterinaria = propietario.getVeterinaria();

        // 3. Cargar productos de esa veterinaria
        List<InventarioModel> productos = inventarioRepository.findByVeterinariaId(veterinaria.getId());

        // 4. Agregar atributos al modelo
        model.addAttribute("productos", productos);
        model.addAttribute("propietario", propietario);

        return "propietario/Tienda";
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

    @GetMapping("/misMascotas")
    public String misMascotas(HttpSession session, Model model) {

        // Obtener propietario en sesión
        PropietarioModel propietario = (PropietarioModel) session.getAttribute("propietario");

        if (propietario == null) {
            return "redirect:/login";
        }

        // Obtener solo las mascotas del propietario
        List<MascotaModel> misMascotas =
                mascotaRepository.findByPropietario(propietario);

        model.addAttribute("listaMascotas", mascotaRepository.findByPropietario(propietario));

        return "propietario/misMascotas";
    }

    @PostMapping("/actualizar")
    public String actualizarMascota(MascotaModel mascota, HttpSession session) {

        PropietarioModel propietario = (PropietarioModel) session.getAttribute("propietario");
        if (propietario == null) {
            return "redirect:/login";
        }

        mascota.setPropietario(propietario);
        mascotaRepository.save(mascota);
        return "redirect:/propietario/misMascotas";
    }
//    @GetMapping("/editar/{id}")
//    public String editarMascota(@PathVariable Long id, Model model) {
//
//        MascotaModel mascota = mascotaRepository.findById(id).orElse(null);
//
//        if (mascota == null) return "redirect:/mascotas/misMascotas";
//
//        model.addAttribute("mascota", mascota);
//
//        return "propietario/editarMascota"; // Vista para modal o página
//    }

}
