package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "propietario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PropietarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    // Relación con Mascota
    @OneToMany(mappedBy = "propietario", fetch = FetchType.EAGER) // Cambiar a EAGER
    private List<MascotaModel> mascotas;
}
