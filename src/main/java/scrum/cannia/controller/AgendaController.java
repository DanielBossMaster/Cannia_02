package scrum.cannia.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.model.*;
import scrum.cannia.repository.AgendaRepository;
import scrum.cannia.repository.ServicioRepository;
import scrum.cannia.service.AgendaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private AgendaService agendaService;

    @PostMapping("/confirmar")
    public String guardarAgenda(
            @RequestParam Integer servicioId,
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime hora,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getPropietario() == null) {
            return "redirect:/login";
        }

        PropietarioModel propietario = usuario.getPropietario();

        ServicioModel servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        VeterinariaModel veterinaria = servicio.getVeterinaria();

        // ⛔ Validar horario duplicado
        boolean ocupado = agendaRepository.existsByServicioAndFechaAndHoraAndEstado(
                servicio, fecha, hora, "AGENDADA"
        );

        if (ocupado) {
            redirect.addFlashAttribute("error",
                    "⛔ Ese horario ya está ocupado, elige otro.");
            return "redirect:/veterinario/Servicios";
        }

        // ✅ Crear cita
        AgendaModel agenda = new AgendaModel();
        agenda.setServicio(servicio);
        agenda.setVeterinaria(veterinaria);
        agenda.setPropietario(propietario);
        agenda.setFecha(fecha);
        agenda.setHora(hora);
        agenda.setEstado("AGENDADA");

        agendaRepository.save(agenda);

        redirect.addFlashAttribute("success",
                "✅ Cita agendada correctamente");

        return "redirect:/veterinario/Servicios";
    }

    @GetMapping("/citas/modal")
    public String cargarCitasModal(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getPropietario() == null) {
            return "veterinario/Fragmentos/CitasVacio :: contenido";
        }

        PropietarioModel propietario = usuario.getPropietario();

        model.addAttribute(
                "citas",
                agendaService.citasPorPropietario(propietario)
        );

        return "veterinario/Fragmentos/CitasModal :: contenido";
    }



}


