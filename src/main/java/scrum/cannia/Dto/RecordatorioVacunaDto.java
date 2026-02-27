package scrum.cannia.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RecordatorioVacunaDto {

    private String nombreMascota;
    private String nombreVacuna;
    private LocalDate fechaRefuerzo;
    private long diasRestantes;
    private boolean vencida;


}