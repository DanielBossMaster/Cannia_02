package scrum.cannia.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.CitaModel;
import scrum.cannia.model.EstadoCita;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.CitaRepository;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.CitaService;

import java.time.LocalDate;
import java.time.LocalTime;
@Controller
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;
    private final UsuarioRepository usuarioRepository;

    /* ===============================
              PROPIETARIO
    =============================== */

    @PostMapping("/agendar")
    @ResponseBody
    public ResponseEntity<?> agendarCita(
            @RequestParam Long mascotaId,
            @RequestParam Long vacunaId,
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime hora,
            Authentication authentication
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        citaService.agendarCita(
                mascotaId,
                vacunaId,
                fecha,
                hora,
                usuario
        );

        return ResponseEntity.ok("Cita agendada correctamente");
    }

    /* ===============================
               VETERINARIO
    =============================== */

    @PostMapping("/{id}/aceptar")
    @ResponseBody
    public ResponseEntity<?> aceptar(@PathVariable Long id) {
        citaService.aceptarCita(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/rechazar")
    @ResponseBody
    public ResponseEntity<?> rechazar(
            @PathVariable Long id,
            @RequestParam String mensaje
    ) {
        citaService.rechazarCita(id, mensaje);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/aplicar")
    @ResponseBody
    public ResponseEntity<?> aplicar(@PathVariable Long id) {
        citaService.aplicarVacuna(id);
        return ResponseEntity.ok().build();
    }
}