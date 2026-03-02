package scrum.cannia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import scrum.cannia.Dto.RecordatorioVacunaDto;
import scrum.cannia.model.*;
import scrum.cannia.repository.CitaRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@AllArgsConstructor
@Service
public class VacunaService {

    private final CitaRepository citaRepository;
    private static final int DIAS_PARA_AGENDAR = 30;

    public List<RecordatorioVacunaDto> obtenerRecordatoriosVacunas(
            PropietarioModel propietario
    ) {

        LocalDate hoy = LocalDate.now();
        List<RecordatorioVacunaDto> lista = new ArrayList<>();

        for (MascotaModel mascota : propietario.getMascotas()) {
            for (VacunaModel vacuna : mascota.getVacunas()) {

                if (vacuna.getFechaRefuerzo() == null) continue;

                long diasRestantes =
                        ChronoUnit.DAYS.between(hoy, vacuna.getFechaRefuerzo());

                if (diasRestantes <= DIAS_PARA_AGENDAR || diasRestantes < 0) {

                    RecordatorioVacunaDto dto = new RecordatorioVacunaDto();

                    CitaModel cita = citaRepository
                            .findTopByVacuna_IdOrderByIdDesc(vacuna.getId())
                            .orElse(null);

                    if (cita == null) {

                        // 🔹 No hay cita
                        dto.setEstadoCita(null);
                        dto.setMensaje(null);

                    } else if (cita.getEstado() == EstadoCita.VACUNA_APLICADA) {

                        // 🔹 Vacuna aplicada → mostrar solo por 24h
                        LocalDateTime limite =
                                cita.getFechaEstado().plusHours(24);

                        if (LocalDateTime.now().isAfter(limite)) {
                            // ⛔ Pasaron 24h → ocultar mensaje
                            dto.setEstadoCita(null);
                            dto.setMensaje(null);
                        } else {
                            // ✅ Aún dentro de 24h
                            dto.setEstadoCita(EstadoCita.VACUNA_APLICADA);
                            dto.setMensaje("Vacuna aplicada correctamente");
                        }

                    } else {
                        // 🔹 Estados normales
                        dto.setEstadoCita(cita.getEstado());
                        dto.setMensaje(cita.getMensaje());
                    }

                    dto.setIdMascota(mascota.getId());
                    dto.setIdVacuna(vacuna.getId());

                    dto.setNombreMascota(mascota.getNomMascota());
                    dto.setNombreVacuna(vacuna.getNombre());
                    dto.setFechaRefuerzo(vacuna.getFechaRefuerzo());
                    dto.setDiasRestantes(diasRestantes);
                    dto.setVencida(diasRestantes < 0);
                    dto.setPermiteAgendar(diasRestantes <= DIAS_PARA_AGENDAR);

                    lista.add(dto);
                }
            }
        }

        return lista;
    }
}