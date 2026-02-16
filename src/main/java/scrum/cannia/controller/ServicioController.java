package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.UsuarioModel;

import scrum.cannia.repository.ServicioRepository;

import scrum.cannia.service.ServicioService;

@AllArgsConstructor
@Controller
@RequestMapping("/inventario/servicios")
public class ServicioController {

    private final ServicioRepository servicioRepository;
    private final ServicioService servicioService;

    // ============================================
    //         GUARDAR SERVICIO
    // ============================================

    @PostMapping("/guardar")
    public String guardar(@Validated @ModelAttribute ServicioModel servicio,
                          BindingResult br,
                          HttpSession session,
                          Model model
    ) {


        // Obtener el usuario en sesión
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";  // Seguridad
        }

        // Validación del formulario
        if (br.hasErrors()) {
            model.addAttribute("mensaje", "Error al insertar los datos.");
            model.addAttribute("servicio", new ServicioModel());
            model.addAttribute("servicios", servicioService.listarTodos());
            return "/Inventario/Servicio";

        }

        servicioRepository.save(servicio);

        return "redirect:/inventario/productos";
        // ruta de la pagina, para que vuelva a la misma pagina
    }

    @GetMapping("/editar/{id}")
    public String editarServicio(@PathVariable Integer id, Model model) {
        ServicioModel serv = servicioService.buscarPorId(id);
        model.addAttribute("servicio", serv);
        return "Inventario/EditarServicio";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute ServicioModel servicio) {
        servicioService.actualizar(servicio);
        return "redirect:/inventario/servicios";
    }

}
