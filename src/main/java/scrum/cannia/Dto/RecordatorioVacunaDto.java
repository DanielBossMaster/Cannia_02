package scrum.cannia.Dto;

import lombok.Data;
import scrum.cannia.model.EstadoCita;

import java.time.LocalDate;

@Data
public class RecordatorioVacunaDto {

    private String nombreMascota;
    private String nombreVacuna;
    private LocalDate fechaRefuerzo;
    private long diasRestantes;
    private boolean vencida;
    private boolean permiteAgendar;

    private Long idMascota;
    private Long idVacuna;
    private EstadoCita estadoCita; // null si no hay cita
    private String mensaje;


}