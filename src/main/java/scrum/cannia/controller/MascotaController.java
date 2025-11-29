package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

}
