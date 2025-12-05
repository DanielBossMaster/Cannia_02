package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.service.MascotaService;
import java.util.List;

@Controller
@RequestMapping("/adopcion")
public class AdopcionController {

    @Autowired
    private MascotaService mascotaService;

    // 1. Vista principal de adopciones con filtro
    @GetMapping
    public String vistaAdopcion(@RequestParam(required = false) String especie, Model model) {
        String welcomeMessage = "Bienvenido/a, esperamos que encuentres a tu nuevo amigo.";

        List<MascotaModel> mascotasEnAdopcion = mascotaService.listarMascotasEnAdopcionPorEspecie(especie);

        model.addAttribute("mascotas", mascotasEnAdopcion);
        model.addAttribute("welcomeMessage", welcomeMessage);
        model.addAttribute("especieSeleccionada", especie != null ? especie : "");

        // Asumiendo que usas 'Canino' y 'Felino' para filtrar
        model.addAttribute("caninoEspecie", "Canino");
        model.addAttribute("felinoEspecie", "Felino");

        return "adopcion/vistaAdopcion";
    }

    // 2. Muestra el formulario de legalización de adopción
    @GetMapping("/legalizar/{mascotaId}")
    public String mostrarFormularioAdopcion(@PathVariable Long mascotaId, Model model) {
        MascotaModel mascota = mascotaService.obtenerPorId(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada para adopción"));

        model.addAttribute("mascota", mascota);
        // Se asume que el formulario enviará los datos a otro endpoint (e.g., /adopcion/procesar)

        return "adopcion/formularioAdopcion";
    }
}