package scrum.cannia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final MascotaRepository mascotaRepository;
    private final VacunaRepository vacunaRepository;
    private final CitaRepository citaRepository;


    public void agendarCita(
            Long mascotaId,
            Long vacunaId,
            LocalDate fecha,
            LocalTime hora,
            UsuarioModel usuario
    ) {

        MascotaModel mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));

        //  Seguridad: la mascota debe pertenecer al usuario
        if (!mascota.getPropietario().getId()
                .equals(usuario.getPropietario().getId())) {
            throw new SecurityException("No autorizado");
        }


        // ❌ ya existe cita activa
        boolean existe = citaRepository.existsByVacunaIdAndEstadoIn(
                vacunaId,
                List.of(EstadoCita.AGENDADA, EstadoCita.ACEPTADA)
        );

        if (existe) {
            throw new IllegalArgumentException("Ya existe una cita para esta vacuna");
        }

        //  Fecha pasada
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha inválida");
        }

        //  Validar horario de atención
        boolean horarioValido =
                (hora.isAfter(LocalTime.of(6, 59)) && hora.isBefore(LocalTime.of(12, 1)))
                        || (hora.isAfter(LocalTime.of(12, 59)) && hora.isBefore(LocalTime.of(17, 1)));

        if (!horarioValido) {
            throw new IllegalArgumentException("Horario fuera de atención");
        }

        VacunaModel vacuna = vacunaRepository.findById(vacunaId)
                .orElseThrow(() -> new IllegalArgumentException("Vacuna no encontrada"));

        CitaModel cita = new CitaModel();
        cita.setMascota(mascota);
        cita.setVacuna(vacuna);
        cita.setFechaCita(fecha);
        cita.setHoraCita(hora);
        cita.setEstado(EstadoCita.AGENDADA);

        citaRepository.save(cita);
    }
}