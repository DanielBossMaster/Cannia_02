package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(length = 20, nullable = false)
    private String usuario;

    @Column(length = 20, nullable = false)
    private String contrasena;

    @Column(length = 20, nullable = false)
    private String rol;

    @OneToOne
    @JoinColumn(name = "id_propietario")
    private PropietarioModel propietario;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private VeterinarioModel veterinario;
}
