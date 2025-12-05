package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pet")
    private int id;

    @Column(length = 50, nullable = false)
    private String nombrePet;

    @Column(length = 50, nullable = false)
    private String razaPet;

    @Column(length = 50, nullable = false)
    private int edadPet;

    @Column(length = 50, nullable = false)
    private String colorPet;

    public PetModel(String nombrePet, String razaPet, int edadPet, String colorPet) {
        this.nombrePet = nombrePet;
        this.razaPet = razaPet;
        this.edadPet = edadPet;
        this.colorPet = colorPet;
    }

}
