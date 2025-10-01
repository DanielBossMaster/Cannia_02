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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Lote")
    private String Lote;

    @Column(name = "Fecha_aplicacion", nullable = false)
    private LocalDate FechaAplicacion;

    @Column(name = "Fecha_refuerzo", nullable = false)
    private LocalDate FechaRefuerzo;

    @Column(name = "Fecha_vencimiento", nullable = false)
    private LocalDate FechaVencimiento;

    @Column(name = "Laboratorio", length = 500, nullable = false)
    private String laboratorio;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para traer la mascota automáticamente
    @JoinColumn(name = "id_mascota", nullable = false)
    private MascotaModel mascota;


}