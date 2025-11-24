package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.service.VeterinarioService.FundacionService;
import scrum.cannia.service.VeterinarioService.MascotaService;

@Controller
@RequestMapping("/fundaciones/{fundacionId}/mascotas")
public class MascotaFundacionController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private FundacionService fundacionService;


    // listar
    @GetMapping
    public String listar(@PathVariable Long fundacionId, Model model) {
        FundacionModel fundacion = fundacionService.obtenerFundacionPorId(fundacionId);
        model.addAttribute("fundacion", fundacion);
        model.addAttribute("mascotas", mascotaService.listarPorFundacion(fundacionId));
        return "mascotas/listar";
    }

    // formulario de creacion
    @GetMapping("/crear")
    public String crear(@PathVariable Long fundacionId, Model model) {
        MascotaModel mascota = new MascotaModel();
        model.addAttribute("mascota", mascota);
        model.addAttribute("fundacionId", fundacionId);
        return "mascotas/form";
    }

    // guardar
    @PostMapping("/guardar")
    public String guardar(@PathVariable Long fundacionId,
                          @ModelAttribute("mascota") MascotaModel mascota) {

        mascotaService.guardarEnFundacion(fundacionId, mascota);

        return "redirect:/fundaciones/" + fundacionId + "/mascotas";
    }

    // editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long fundacionId,
                         @PathVariable Long id,
                         Model model) {

        MascotaModel mascota = mascotaService.obtenerPorId(id).orElse(null);
        model.addAttribute("mascota", mascota);
        model.addAttribute("fundacionId", fundacionId);
        return "mascotas/form";
    }

    // actualizar
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long fundacionId,
                             @PathVariable Long id,
                             @ModelAttribute("mascota") MascotaModel mascota) {

        mascotaService.actualizarMascota(id, mascota);

        return "redirect:/fundaciones/" + fundacionId + "/mascotas";
    }

    // eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long fundacionId,
                           @PathVariable Long id) {

        mascotaService.eliminarMascota(id);

        return "redirect:/fundaciones/" + fundacionId + "/mascotas";
    }
}
