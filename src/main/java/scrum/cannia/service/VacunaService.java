package scrum.cannia.service;

import org.springframework.stereotype.Service;
import scrum.cannia.Dto.RecordatorioVacunaDto;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.VacunaModel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class VacunaService {

    private static final int DIAS_AVISO = 1000;

    public List<RecordatorioVacunaDto> obtenerRecordatoriosVacunas(
            PropietarioModel propietario
    ) {

        List<RecordatorioVacunaDto> recordatorios = new ArrayList<>();

        if (propietario == null || propietario.getMascotas() == null) {
            return recordatorios;
        }

        LocalDate hoy = LocalDate.now();

        for (MascotaModel mascota : propietario.getMascotas()) {

            if (mascota.getVacunas() == null) continue;

            for (VacunaModel vacuna : mascota.getVacunas()) {

                if (vacuna.getFechaRefuerzo() == null) continue;

                long diasRestantes =
                        ChronoUnit.DAYS.between(hoy, vacuna.getFechaRefuerzo());

                // 👉 mostrar solo vencidas o próximas
                if (diasRestantes <= DIAS_AVISO) {

                    RecordatorioVacunaDto dto = new RecordatorioVacunaDto();
                    dto.setNombreMascota(mascota.getNomMascota());
                    dto.setNombreVacuna(vacuna.getNombre());
                    dto.setFechaRefuerzo(vacuna.getFechaRefuerzo());
                    dto.setDiasRestantes(diasRestantes);
                    dto.setVencida(diasRestantes < 0);

                    recordatorios.add(dto);
                }
            }
        }

        // 🔥 Ordenar: primero vencidas, luego las más próximas
        recordatorios.sort(
                Comparator
                        .comparing(RecordatorioVacunaDto::isVencida).reversed()
                        .thenComparing(RecordatorioVacunaDto::getFechaRefuerzo)
        );

        return recordatorios;
    }
}