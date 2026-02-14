package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.AdminService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private  AdminService adminService;

    @GetMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("pendientes", adminService.obtenerPendientes());
        return "admin/index";
    }

    @PostMapping("/aprobar/{id}")
    public String aprobarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes ) {
        adminService.aprobarUsuario(id);
        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Usuario aprobado correctamente "
        );
        return "redirect:/admin/index";
    }

    @PostMapping("/rechazar/{id}")
    public String rechazarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.rechazarUsuario(id);
        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Usuario rechazado correctamente "
        );

        return "redirect:/admin/index";
    }
}

