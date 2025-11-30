package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer id;

    @Column(length = 50, nullable = false)
    private String nombre;  // ← CAMBIA 'Nombre' por 'nombre' (minúscula)

    @Column(length = 800, nullable = false)
    private String descripcion;  // ← también cambia a minúscula si es necesario

    @Column(nullable = false)
    private Integer duracionEstimada;

    @Column(nullable = false)
    private Integer precio;

    @ManyToOne
    @JoinColumn(name = "id_veterinaria")
    private VeterinariaModel veterinaria;
}
