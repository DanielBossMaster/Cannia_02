package scrum.cannia.controller;

import scrum.cannia.model.FundacionModel;
import scrum.cannia.service.VeterinarioService.FundacionService;
import scrum.cannia.model.MascotaModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/fundaciones")
public class FundacionController {

    @Autowired
    private FundacionService fundacionService;

    // RUTA RAÍZ - REDIRIGE AL LISTADO
    @GetMapping("")
    public String redirigirAListar() {
        return "redirect:/fundaciones/listar";
    }

    // LISTAR FUNDACIONES
    @GetMapping("/listar")
    public String listarFundaciones(Model model) {
        model.addAttribute("fundaciones", fundacionService.listarFundaciones());
        return "fundaciones/listar";
    }

    // FORMULARIO DE CREACIÓN
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("fundacion", new FundacionModel());
        return "fundaciones/form";
    }

    // GUARDAR FUNDACIÓN
    @PostMapping("/guardar")
    public String guardarFundacion(@Valid @ModelAttribute("fundacion") FundacionModel fundacion,
                                   BindingResult result) {

        if (result.hasErrors()) {
            return "fundaciones/form";
        }

        fundacionService.guardarFundacion(fundacion);
        return "redirect:/fundaciones/listar";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editarFundacion(@PathVariable Long id, Model model) {
        FundacionModel fundacion = fundacionService.obtenerFundacionPorId(id);

        if (fundacion == null) {
            return "redirect:/fundaciones/listar";
        }

        model.addAttribute("fundacion", fundacion);
        return "fundaciones/form";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String actualizarFundacion(@PathVariable Long id,
                                      @Valid @ModelAttribute("fundacion") FundacionModel fundacion,
                                      BindingResult result) {

        if (result.hasErrors()) {
            return "fundaciones/form";
        }

        fundacionService.actualizarFundacion(id, fundacion);
        return "redirect:/fundaciones/listar";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarFundacion(@PathVariable Long id) {
        fundacionService.eliminarFundacion(id);
        return "redirect:/fundaciones/listar";
    }

    // DETALLE DE FUNDACIÓN
    @GetMapping("/detalle/{id}")
    public String detalleFundacion(@PathVariable Long id, Model model) {

        FundacionModel fundacion = fundacionService.obtenerFundacionPorId(id);

        if (fundacion == null) {
            return "redirect:/fundaciones/listar";
        }

        model.addAttribute("fundacion", fundacion);
        return "fundaciones/detalle"; // templates/fundaciones/detalle.html
    }
}