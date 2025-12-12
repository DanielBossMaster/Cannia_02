package scrum.cannia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.ErrorCargaDTO;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.Dto.ResultadoCargaMascotasDTO;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.service.MascotaService;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.strategy.DataLoaderStrategy;
import scrum.cannia.strategy.factory.DataLoaderFactory;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/fundacion")
public class FundacionController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private MascotaRepository mascotaRepository;

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
            HttpSession session,
            Model model) {

        try {

            // Obtener la fundación desde la sesión
            Long fundacionId = (Long) session.getAttribute("fundacionId");

            if (fundacionId == null) {
                model.addAttribute("error", "Debe iniciar sesión para cargar mascotas.");
                return "fundacion/CargarMascotas";
            }

            String filename = file.getOriginalFilename();
            DataLoaderStrategy strategy = DataLoaderFactory.getStrategy(filename);

            List<MascotaCargaDTO> mascotas = strategy.loadData(file);

            ResultadoCargaMascotasDTO resultado =
                    mascotaService.guardarMascotasDesdeFundacion(mascotas, fundacionId);

            model.addAttribute("mensaje",
                    "Archivo procesado. Guardadas: " + resultado.getGuardadas().size());

            if (!resultado.getErrores().isEmpty()) {
                StringBuilder sb = new StringBuilder("Se encontraron errores:\n");

                for (ErrorCargaDTO err : resultado.getErrores()) {
                    sb.append("Fila ").append(err.getFila())
                            .append(": ").append(err.getMensaje()).append("\n");
                }

                model.addAttribute("error", sb.toString());
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "fundacion/CargarMascotas";
    }


    @GetMapping("/mascotasCargadas")
    public String mascotasCargadas(Model model, HttpSession session) {

        Long fundacionId = (Long) session.getAttribute("fundacionId");

        if (fundacionId == null) {
            return "redirect:/login";
        }

        List<MascotaModel> mascotas = mascotaRepository.findByFundacion_Id(fundacionId);

        model.addAttribute("mascotas", mascotas);

        return "fundacion/MascotasCargadas";
    }


}