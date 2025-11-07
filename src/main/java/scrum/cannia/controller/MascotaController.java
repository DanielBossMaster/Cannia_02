package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
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

    @GetMapping
    String index(Model model) {

        List<MascotaModel> mascotas = mascotaRepository.findAll();
        List<PropietarioModel> propietarios = propietarioRepository.findByEstadoTrue();

        model.addAttribute("mascotas", mascotas);
        model.addAttribute("propietarios", propietarios);
        model.addAttribute("mascota", new MascotaModel());

        return "propietario/indexPropietario";
    }

    @PostMapping("/nuevom")
    public String registrarMascota(@ModelAttribute MascotaModel mascota,
                                   @RequestParam("numDoc") String numDoc,
                                   Model model) {

        // Buscar propietario por cédula
        PropietarioModel propietario = propietarioRepository.findByNumDoc(numDoc);

        if (propietario != null) {
            mascota.setPropietario(propietario);
            mascotaRepository.save(mascota);
            model.addAttribute("mensaje", "Mascota registrada correctamente.");
        } else {
            model.addAttribute("error", "No se encontró propietario con esa cédula.");
        }
        return "redirect:/mascotas";
    }
}