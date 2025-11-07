package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="veterinario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_veterinario")
    private int id;

    @Column(length = 50, nullable = false)
    private String numLicencia;

    @Column(length = 50, nullable = false)
    private String nombreVete;

    @Column(length = 50, nullable = false)
    private String apellidoVete;

    @Column(length = 50, nullable = false)
    private String direccionVete;

    @Column(length = 50, nullable = false)
    private String telefonoVete;

    @Column(length = 50, nullable = false)
    private String correoVete;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private UsuarioModel usuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_veterinaria", referencedColumnName = "id_veterinaria")
    private VeterinariaModel veterinaria;
}
