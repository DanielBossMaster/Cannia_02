package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historia_clinica")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinicaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historia_clinica")
    private Long idHistoriaClinica;

    /**
     * Relación con la mascota.
     * Esto te permite acceder a los campos de Mascota desde la historia clínica,
     * ej: historia.getMascota().getNomMascota().
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mascota", nullable = false)
    private MascotaModel mascota;

    @Column(name = "peso", nullable = false)
    private Float peso;

    @Column(name = "anamnesis", length = 1000, nullable = false)
    private String anamnesis;

    @Column(name = "diagnostico", length = 1000, nullable = false)
    private String diagnostico;

    @Column(name = "tratamiento", length = 1000, nullable = false)
    private String tratamiento;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();
}

