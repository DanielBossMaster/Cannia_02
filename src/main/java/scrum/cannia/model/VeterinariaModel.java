package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "veterinaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinariaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_veterinaria")
    private int id;

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(length = 50, nullable = false)
    private String correo;

    @Column(length = 50, nullable = false)
    private String telefono;

    @OneToOne(mappedBy = "veterinaria", cascade = CascadeType.ALL)
    private VeterinarioModel veterinario;


}
