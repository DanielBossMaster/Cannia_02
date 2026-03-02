package scrum.cannia.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scrum.cannia.Dto.CitaVeterinarioDto;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


        boolean existeActiva = citaRepository.existsByVacunaIdAndEstadoIn(
                vacunaId,
                List.of(
                        EstadoCita.AGENDADA,
                        EstadoCita.ACEPTADA
                )
        );

        if (existeActiva) {
            throw new IllegalArgumentException(
                    "Ya existe una cita en proceso para esta vacuna"
            );
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

    @Transactional
    public void aplicarVacuna(Long citaId) {

        CitaModel cita = citaRepository.findById(citaId)
                .orElseThrow();

        if (cita.getEstado() != EstadoCita.ACEPTADA) {
            throw new IllegalStateException("La cita no está aceptada");
        }

        VacunaModel vacuna = cita.getVacuna();

        // 1. Marcar cita como finalizada
        cita.setEstado(EstadoCita.VACUNA_APLICADA);
        cita.setFechaEstado(LocalDateTime.now());

        // 2. Registrar aplicación de vacuna
        vacuna.setFechaAplicacion(LocalDate.now());
        vacuna.setFechaRefuerzo(
                LocalDate.now().plusMonths(12) // o lo que definas
        );

        citaRepository.save(cita);
        vacunaRepository.save(vacuna);
    }

    public List<CitaModel> obtenerCitasPendientes(VeterinarioModel veterinario) {
        return citaRepository.findByEstadoInAndVacuna_Mascota_Propietario_Veterinario(
                List.of(
                        EstadoCita.AGENDADA,
                        EstadoCita.ACEPTADA
                ),
                veterinario
        );
    }

    public List<CitaVeterinarioDto> obtenerCitasPendientes() {

        return citaRepository
                .findByEstadoOrderByFechaCitaAscHoraCitaAsc(EstadoCita.AGENDADA)
                .stream()
                .map(cita -> {

                    CitaVeterinarioDto dto = new CitaVeterinarioDto();

                    dto.setCitaId(cita.getId());
                    dto.setNombreMascota(cita.getMascota().getNomMascota());
                    dto.setNombrePropietario(
                            cita.getMascota().getPropietario().getNombrePro()
                    );
                    dto.setNombreVacuna(cita.getVacuna().getNombre());
                    dto.setFecha(cita.getFechaCita());
                    dto.setHora(cita.getHoraCita());
                    dto.setEstado(cita.getEstado());
                    dto.setMensaje(cita.getMensaje());

                    return dto;
                })
                .toList();
    }


    public void aceptarCita(Long citaId) {
        CitaModel cita = citaRepository.findById(citaId)
                .orElseThrow();

        if (cita.getEstado() != EstadoCita.AGENDADA) {
            throw new IllegalStateException("Solo se pueden aceptar citas solicitadas");
        }

        cita.setEstado(EstadoCita.ACEPTADA);
        cita.setMensaje(null);
        citaRepository.save(cita);
    }

    public void rechazarCita(Long citaId, String mensaje) {
        CitaModel cita = citaRepository.findById(citaId)
                .orElseThrow();

        if (cita.getEstado() != EstadoCita.AGENDADA) {
            throw new IllegalStateException("Solo se pueden rechazar citas solicitadas");
        }

        cita.setEstado(EstadoCita.RECHAZADA);
        cita.setMensaje(mensaje);
        citaRepository.save(cita);
    }
}