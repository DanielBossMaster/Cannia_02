package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "cita")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mascota asociada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private MascotaModel mascota;

    // Vacuna asociada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacuna_id", nullable = false)
    private VacunaModel vacuna;

    // Fecha agendada
    @Column(nullable = false)
    private LocalDate fechaCita;

    // Estado de la cita
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado;


    // CitaModel.java
    @Column(nullable = false)
    private LocalTime horaCita;

    @Column(length = 500)
    private String mensaje;
}