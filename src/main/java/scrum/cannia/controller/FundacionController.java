package scrum.cannia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.dto.MascotaCargaDTO;
import scrum.cannia.service.MascotaService;
import scrum.cannia.strategy.DataLoaderStrategy;
import scrum.cannia.strategy.factory.DataLoaderFactory;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/fundacion")
public class FundacionController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // Validar sesión
        Object fundacionId = session.getAttribute("fundacionId");
        Object fundacionNombre = session.getAttribute("fundacionNombre");

        if (fundacionId == null) {
            return "redirect:/login";
        }

        // Enviar datos básicos a la vista
        model.addAttribute("fundacionId", fundacionId);
        model.addAttribute("fundacionNombre", fundacionNombre);

        return "fundacion/dashboard";
    }

    @GetMapping("/CargarMascotas")
    public String vistaCargarMascotas() {
        return "fundacion/CargarMascotas";
    }

    @PostMapping("/upload")
    public String cargarMascotasFundacion(
            @RequestParam("archivo") MultipartFile file,
            Principal principal,
            Model model) {

        try {
            String filename = file.getOriginalFilename();

            DataLoaderStrategy strategy = DataLoaderFactory.getStrategy(filename);

            List<MascotaCargaDTO> mascotas = strategy.loadData(file);

            mascotaService.guardarMascotasDesdeFundacion(
                    mascotas,
                    principal.getName()  // username logueado
            );

            model.addAttribute("mensaje",
                    "Archivo cargado correctamente. Registros: " + mascotas.size());

        } catch (Exception e) {
            model.addAttribute("mensaje",
                    "Error: " + e.getMessage());
        }

        return "fundacion/dashboard";
    }

}