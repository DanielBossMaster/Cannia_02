package scrum.cannia.model;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import scrum.cannia.Dto.MascotaCargaDTO;

import java.util.Date;

@Entity
@Table(name = "mascota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long id;

    @Column(length = 45, nullable = false)
    private String nomMascota;

    @Column(length = 45, nullable = false)
    private String especie;

    @Column(length = 45, nullable = false)
    private String raza;

    @Column(name = "fecha_nacimiento")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    @Column(name = "fecha_vacunacion")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaVacunacion;

    @Column(length = 45)
    private String medicamento;

    @Column(length = 20)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Genero genero;

    @ManyToOne
    @JoinColumn(name = "id_propietario", nullable = true)
    private PropietarioModel propietario;

    @ManyToOne
    @JoinColumn(name = "id_fundacion",nullable = true)
    private FundacionModel fundacion;

    @Column(length = 50)
    private String edadFundacion;

    @Column(length = 250)
    private String foto;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = true)
    private TipoEstadoMascota tipoEstado;

    // Factory Method mascota creada por Fundacion
    public static MascotaModel crearDesdeFundacion(MascotaCargaDTO dto, FundacionModel fundacion) {
        MascotaModel mascota = new MascotaModel();

        // Campos obligatorios
        mascota.setNomMascota(dto.getNombre());
        mascota.setEspecie(dto.getEspecie() != null ? dto.getEspecie() : "Desconocida");
        mascota.setRaza(dto.getRaza());
        mascota.setColor(dto.getColor());
        mascota.setGenero(dto.getGenero());
        mascota.setTipoEstado(dto.getTipoEstado());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setFechaVacunacion(dto.getFechaVacunacion());

        // Campos opcionales con valores por defecto
        mascota.setEdadFundacion(dto.getEdad() != null ? String.valueOf(dto.getEdad()) : "0");
        mascota.setFoto(dto.getFoto() != null ? dto.getFoto() : "");
        mascota.setMedicamento(dto.getMedicamento() != null ? dto.getMedicamento() : "");

        // Relaciones
        mascota.setFundacion(fundacion);
        mascota.setPropietario(null);

        return mascota;
    }

    //  Factory Method mascota creada por Propietario
    public static MascotaModel crearParaPropietario(
            String nombre,
            String especie,
            String raza,
            Genero genero,
            String color,
            PropietarioModel propietario) {

        MascotaModel mascota = new MascotaModel();
        mascota.nomMascota = nombre;
        mascota.especie = especie;
        mascota.raza = raza;
        mascota.genero = genero;
        mascota.color = color;
        mascota.propietario = propietario;
        mascota.fundacion = null;
        mascota.tipoEstado = TipoEstadoMascota.PROPIA;

        return mascota;
    }

}
