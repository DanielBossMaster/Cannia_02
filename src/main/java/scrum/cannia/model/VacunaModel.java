package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vacuna")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacunaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="Nombre", nullable = false)
    private String nombre;

    @Column(name = "Lote")
    private String Lote;

    @Column(name = "Fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(name = "Fecha_refuerzo", nullable = false)
    private LocalDate fechaRefuerzo;

    @Column(name = "Fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "Laboratorio", length = 500, nullable = false)
    private String laboratorio;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para traer la mascota automáticamente
    @JoinColumn(name = "id_mascota", nullable = false)
    private MascotaModel mascota;


}