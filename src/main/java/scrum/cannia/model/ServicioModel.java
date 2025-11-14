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
    private int id;

    @Column(length = 50, nullable = false)
    private String Nombre;

    @Column(length = 800, nullable = false)
    private String Descripcion;

    @Column(length = 50, nullable = false)
    private String Disponibilidad;

    @Column( nullable = false)
    private Integer DuracionEstimada;

    @Column( nullable = false)
    private Integer Precio;

    @ManyToOne
    @JoinColumn(name = "id_veterinaria" )
    private VeterinariaModel veterinaria;
}
