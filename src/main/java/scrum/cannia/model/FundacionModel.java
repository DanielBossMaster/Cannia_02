package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Fundacion")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class FundacionModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private String Nombre;

    @Column(length = 50, nullable = false)
    private String Correo;

    @Column(length = 50, nullable = false)
    private String Telefono;
}
