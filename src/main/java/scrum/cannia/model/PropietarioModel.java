package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "propietario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PropietarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propietario")
    private int id;

    @Column(length = 20, nullable = false)
    private String numDoc;

    @Column(length = 45, nullable = false)
    private String nombrePro;

    @Column(length = 45, nullable = false)
    private String apellidoPro;

    @Column(length = 45, nullable = false)
    private String direccionPro;

    @Column(length = 20, nullable = false)
    private String telefonoPro;

    @Column(length = 45, nullable = false)
    private String correoPro;

    @Column(nullable = false)
    private boolean estado = true;

    // Relación con Mascota
    @OneToMany(mappedBy = "propietario", fetch = FetchType.LAZY) // Cambiar a EAGER
    private List<MascotaModel> mascotas;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private UsuarioModel usuario;

    @ManyToOne
    @JoinColumn(name = "id_veterinaria")
    private VeterinariaModel veterinaria;

}