package scrum.cannia.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="veterinaria")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class VeterinariaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name = "id_veterinaria")
    private int id;

    @Column(length = 50, nullable = false)
    private String Nombre;

    @Column(length = 50, nullable = false)
    private String Correo;

    @Column(length = 50, nullable = false)
    private String Telefono;

    @OneToOne(mappedBy = "veterinaria")
    private VeterinarioModel veterinario;

}
