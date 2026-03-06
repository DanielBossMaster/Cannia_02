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
    private static final int DIAS_PARA_AGENDAR = 300;

    public List<RecordatorioVacunaDto> obtenerRecordatoriosVacunas(PropietarioModel propietario) {

        LocalDate hoy = LocalDate.now();
        List<RecordatorioVacunaDto> lista = new ArrayList<>();

        for (MascotaModel mascota : propietario.getMascotas()) {

            for (VacunaModel vacuna : mascota.getVacunas()) {

                if (vacuna.getFechaRefuerzo() == null) {
                    continue;
                }

                long diasRestantes = ChronoUnit.DAYS.between(hoy, vacuna.getFechaRefuerzo());

                if (diasRestantes <= DIAS_PARA_AGENDAR) {

                    RecordatorioVacunaDto dto = new RecordatorioVacunaDto();

                    // buscar última cita
                    CitaModel cita = citaRepository
                            .findTopByVacuna_IdOrderByIdDesc(vacuna.getId())
                            .orElse(null);

                    boolean permiteAgendar = false;

                    if (cita == null) {

                        dto.setEstadoCita(null);
                        dto.setMensaje(null);
                        permiteAgendar = true;

                    } else {

                        dto.setEstadoCita(cita.getEstado());
                        dto.setMensaje(cita.getMensaje());

                        if (cita.getEstado() == EstadoCita.RECHAZADA) {
                            permiteAgendar = true;
                        }
                    }

                    dto.setPermiteAgendar(permiteAgendar);

                    dto.setIdMascota(mascota.getId());
                    dto.setIdVacuna(vacuna.getId());

                    dto.setNombreMascota(mascota.getNomMascota());
                    dto.setNombreVacuna(vacuna.getNombre());

                    dto.setFechaRefuerzo(vacuna.getFechaRefuerzo());
                    dto.setDiasRestantes(diasRestantes);
                    dto.setVencida(diasRestantes < 0);

                    lista.add(dto);
                }
            }
        }

        lista.sort(Comparator.comparingLong(RecordatorioVacunaDto::getDiasRestantes));

        return lista;
    }
}