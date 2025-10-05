package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.PropietarioRepository;

import java.util.List;
import java.util.Optional;

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
    @GetMapping String index(Model model){
        model.addAttribute("mascotas", mascotaRepository.findAll());
        return "propietario/indexPropietario";
    }

    // Listar mascotas de un propietario
    @GetMapping("/propietario/{idPropietario}")
    public String listarMascotasPorPropietario(@PathVariable("idPropietario") Long idPropietario, Model model) {
        List<MascotaModel> mascotas = mascotaRepository.findByPropietarioId(idPropietario);
        Optional<PropietarioModel> propietario = propietarioRepository.findById(idPropietario);

        model.addAttribute("mascotas", mascotas);
        model.addAttribute("propietario", propietario.orElse(null));
        return "indexPropietario"; // Renderiza indexPropietario.html
    }

    @GetMapping("/registrar/{idPropietario}")
    public String registrar(@PathVariable("idPropietario") Long idPropietario, Model model) {
        MascotaModel mascota = new MascotaModel();
        propietarioRepository.findById(idPropietario).ifPresent(mascota::setPropietario);

        model.addAttribute("mascota", mascota);
        return "FormularioMascota";
    }
    @PostMapping("/nuevom")
    public String nuevo(@Validated @ModelAttribute MascotaModel mascotaModel, BindingResult br) {
        if (br.hasErrors()) {
            return "mascotas/indexPropietario";
        } else {
            mascotaRepository.save(mascotaModel);
            return "redirect:/mascotas";
        }


    }

    // Guardar mascota (crear o actualizar)
    @PostMapping("/guardar")
    public String guardarMascota(@ModelAttribute MascotaModel mascota) {
        mascotaRepository.save(mascota);

        // ⚡ Redirigir al listado del propietario al que pertenece la mascota
        return "redirect:/mascotas/propietario/" + mascota.getPropietario().getId();
    }


    // Mostrar formulario para editar mascota
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Optional<MascotaModel> mascota = mascotaRepository.findById(id);
        if (mascota.isPresent()) {
            model.addAttribute("mascota", mascota.get());
            return "EditarMascota"; // Renderiza EditarMascota.html
        } else {
            return "redirect:/mascotas/propietario/";
        }
    }

    // Eliminar mascota
    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable("id") int id) {
        Optional<MascotaModel> mascota = mascotaRepository.findById((long) id);

        if (mascota.isPresent()) {
            int idPropietario = mascota.get().getPropietario().getId();
            mascotaRepository.deleteById((long) id);
            return "redirect:/mascotas/propietario/" + idPropietario;
        }

        return "redirect:/mascotas";
    }

}
