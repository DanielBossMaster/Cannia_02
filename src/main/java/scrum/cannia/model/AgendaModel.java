package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "agenda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agenda")
    private Integer id;

    // ==========================
    // PROPIETARIO
    // ==========================
    @ManyToOne
    @JoinColumn(name = "id_propietario", nullable = false)
    private PropietarioModel propietario;

    // ==========================
    // SERVICIO
    // ==========================
    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private ServicioModel servicio;

    // ==========================
    // VETERINARIA
    // ==========================
    @ManyToOne
    @JoinColumn(name = "id_veterinaria", nullable = false)
    private VeterinariaModel veterinaria;

    // ==========================
    // FECHA Y HORA
    // ==========================
    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    // ==========================
    // ESTADO DE LA CITA
    // ==========================

    @Column(nullable = false)
    private String estado;
// AGENDADA | RECHAZADA | ATENDIDA

}
