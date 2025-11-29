package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Fundacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundacionModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fundacion")
    private Long id;

    @Column(length = 50, nullable = false)
    @NotBlank(message = "El Nombre es Obligatorio")
    private String nombre;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(length = 50, nullable = false)
    private String direccion;

    @Column(length = 50, nullable = false)
    private String telefono;

    @Column(length = 50, nullable = false)
    @Email(message = "Debe ser un correo válido")
    private String email;

    //uso de soft delete
    @Column(nullable = false)
    private boolean estado = true;

    @OneToMany(mappedBy = "fundacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MascotaModel> mascotas = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private UsuarioModel usuario;

}

