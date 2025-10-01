package scrum.cannia.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.PropietarioRepository;
import scrum.cannia.repository.VeterinarioRepository;

@Controller
@RequestMapping("/veterinario")
public class VeterinarioController {

    private final VeterinarioRepository veterinarioRepository;
    private final PropietarioRepository propietarioRepository;
    private final MascotaRepository mascotaRepository;


    public VeterinarioController(
            VeterinarioRepository veterinarioRepository,
            PropietarioRepository propietarioRepository,
            MascotaRepository mascotaRepository) {

        this.propietarioRepository = propietarioRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.mascotaRepository = mascotaRepository;


}



@GetMapping

public String index(Model model) {
    model.addAttribute("veterinarios", veterinarioRepository.findAll());
    model.addAttribute("propietarios", propietarioRepository.findAll());
    model.addAttribute("mascotas", mascotaRepository.findAll());
    model.addAttribute("propietario", new PropietarioModel());
    model.addAttribute("mascota", new MascotaModel());
    return "veterinario/index";


}

@PostMapping("/nuevo")
public String nuevo(@Validated @ModelAttribute PropietarioModel propietarioModel, BindingResult br) {
    if (br.hasErrors()) {
        return "veterinario/index";
    } else {
        propietarioRepository.save(propietarioModel);
        return "redirect:/veterinario";
    }


}

@PostMapping("/nuevom")
public String agregarMascota(@ModelAttribute MascotaModel mascota,
                             @RequestParam long propietarioId) {

    PropietarioModel propietario = propietarioRepository.findById((long) propietarioId)
            .orElseThrow(() -> new IllegalArgumentException("No se encontro porpietario"));

    mascota.setPropietario(propietario);
    propietario.getMascotas().add(mascota);


    mascotaRepository.save(mascota);
    return "redirect:/veterinario";
}


@PostMapping("/borrar/{id}")
public String borrar(@PathVariable long id) {
    mascotaRepository.deleteById(id);
    return "redirect:/veterinario";
}

@PostMapping("/borrarp/{id}")
public String borrarp(@PathVariable long id) {
    propietarioRepository.deleteById(id);
    return "redirect:/veterinario";
}


@PostMapping("/editar/{id}")
public String actualizar(@PathVariable Long id, @ModelAttribute PropietarioModel cambios) {
    PropietarioModel existente = propietarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Propietario no encontrado"));

    // Solo actualiza si el usuario ingresó algo
    if (cambios.getNombrePro() != null && !cambios.getNombrePro().isBlank()) {
        existente.setNombrePro(cambios.getNombrePro());
    }
    if (cambios.getApellidoPro() != null && !cambios.getApellidoPro().isBlank()) {
        existente.setApellidoPro(cambios.getApellidoPro());
    }
    if (cambios.getDireccionPro() != null && !cambios.getDireccionPro().isBlank()) {
        existente.setDireccionPro(cambios.getDireccionPro());
    }
    if (cambios.getTelefonoPro() != null && !cambios.getTelefonoPro().isBlank()) {
        existente.setTelefonoPro(cambios.getTelefonoPro());
    }
    if (cambios.getCorreoPro() != null && !cambios.getCorreoPro().isBlank()) {
        existente.setCorreoPro(cambios.getCorreoPro());
    }

    propietarioRepository.save(existente);
    return "redirect:/veterinario";
}

@PostMapping("/editarm/{id}")
public String actualizar(@PathVariable int id, @ModelAttribute MascotaModel mascotaModel, BindingResult br) {
    if (br.hasErrors()) {
        return "veterinario/index";
    } else {
        mascotaModel.setId(id);
        mascotaRepository.save(mascotaModel);
        return"veterinario/index";
    }
}

    // Muestra la vista propietarioVH
    @GetMapping("/propietarioVH")
    public String mostrarPropietarioVH(Model model) {
        model.addAttribute("propietarios", propietarioRepository.findAll());
        return "veterinario/propietarioVH";
    }

    }
