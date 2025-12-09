package scrum.cannia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import scrum.cannia.model.Genero;
import scrum.cannia.model.TipoEstadoMascota;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaCargaDTO {

    // Obligatorios
    private String nombre;
    private String especie;
    private String raza;
    private String color;

    private Genero genero;
    private TipoEstadoMascota tipoEstado;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaVacunacion;

    // Opcionales
    private Integer edad; // edad en la fundación
    private String foto;
    private String medicamento;
}
