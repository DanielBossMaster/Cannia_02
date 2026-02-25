package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.Dto.ResultadoCargaMascotasDTO;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.MascotaService;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.service.MascotaServiceCreator;
import scrum.cannia.strategy.DataLoaderStrategy;
import scrum.cannia.strategy.factory.DataLoaderFactory;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/fundacion")
public class FundacionController {

    private final MascotaService mascotaService;
    private final MascotaRepository mascotaRepository;
    private final MascotaServiceCreator mascotaServiceCreator;
    private final UsuarioRepository usuarioRepository;;

    @GetMapping("/index")
    public String dashboard(Authentication authentication, Model model) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        FundacionModel fundacion = usuario.getFundacion();

        if (fundacion == null) {
            return "redirect:/login";
        }

        model.addAttribute("fundacion", fundacion);

        return "fundacion/index";
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

        Long fundacionId = (Long) session.getAttribute("fundacionId");

        if (fundacionId == null) {
            model.addAttribute("error", "No hay fundación en sesión.");
            return "redirect:/login";
        }

        try {
            // 1️⃣ Obtener estrategia según extensión
            String filename = file.getOriginalFilename();
            DataLoaderStrategy strategy = DataLoaderFactory.getStrategy(filename);

            // 2️⃣ Leer archivo (Strategy)
            List<MascotaCargaDTO> mascotasDTO = strategy.loadData(file);

            // 3️⃣ Crear mascotas usando TEMPLATE METHOD
            ResultadoCargaMascotasDTO resultado =
                    mascotaServiceCreator.crearDesdeFundacion(
                            mascotasDTO,
                            fundacionId
                    );

            // 4️⃣ Enviar resultados a la vista
            model.addAttribute("total", resultado.getGuardadas().size());
            model.addAttribute("errores", resultado.getErrores());

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "fundacion/CargarMascotas";
    }



    @GetMapping("/mascotasCargadas")
    public String mascotasCargadas(HttpSession session, Model model) {

        Long fundacionId = (Long) session.getAttribute("fundacionId");

//        if (fundacionId == null) {
//            return "redirect:/login";
//        }

        List<MascotaModel> mascotas =
                mascotaRepository.findByFundacion_Id(fundacionId);

        model.addAttribute("mascotas", mascotas);

        return "fundacion/MascotasCargadas";
    }



}