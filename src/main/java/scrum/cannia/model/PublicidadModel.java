package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "publicidad")
public class PublicidadModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    @Column(length = 2000)
    private String mensaje;

    private String imagenUrl; // opcional

    private LocalDate fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_veterinaria")
    private VeterinariaModel veterinaria;
}
