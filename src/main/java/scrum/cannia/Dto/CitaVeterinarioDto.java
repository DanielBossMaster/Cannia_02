package scrum.cannia.Dto;

import lombok.Data;
import scrum.cannia.model.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaVeterinarioDto {

    private Long citaId;

    private String nombreMascota;
    private String nombrePropietario;
    private String nombreVacuna;

    private LocalDate fecha;
    private LocalTime hora;

    private EstadoCita estado;
    private String mensaje;
}