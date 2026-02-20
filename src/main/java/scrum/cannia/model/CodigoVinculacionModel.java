package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "codigo_vinculacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodigoVinculacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 12)
    private String codigo;

    @Column(nullable = false)
    private boolean usado = false;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    // ============================
    // RELACIONES
    // ============================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    private PropietarioModel propietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    private VeterinarioModel veterinario;
}