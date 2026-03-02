package scrum.cannia.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.UsuarioModel;
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

        try {
            citaService.agendarCita(
                    mascotaId,
                    vacunaId,
                    fecha,
                    hora,
                    usuario
            );
            return ResponseEntity.ok("Cita agendada correctamente");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("No autorizado");
        }
    }
}