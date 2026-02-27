package scrum.cannia.service;

import scrum.cannia.Dto.RecordatorioVacunaDto;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.VacunaModel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class VacunaService {

    public List<RecordatorioVacunaDto> obtenerRecordatoriosVacunas(PropietarioModel propietario) {

        LocalDate hoy = LocalDate.now();
        List<RecordatorioVacunaDto> recordatorios = new ArrayList<>();

        for (MascotaModel m : propietario.getMascotas()) {
            for (VacunaModel v : m.getVacunas()) {

                if (v.getFechaRefuerzo() == null) continue;

                long dias = ChronoUnit.DAYS.between(hoy, v.getFechaRefuerzo());

                // mostrar si vence en los próximos 30 días o ya venció
                if (dias <= 30) {
                    RecordatorioVacunaDto dto = new RecordatorioVacunaDto();
                    dto.setNombreMascota(m.getNomMascota());
                    dto.setNombreVacuna(v.getNombre());
                    dto.setFechaRefuerzo(v.getFechaRefuerzo());
                    dto.setDiasRestantes(dias);
                    dto.setVencida(dias < 0);

                    recordatorios.add(dto);
                }
            }
        }

        return recordatorios;
    }
}
