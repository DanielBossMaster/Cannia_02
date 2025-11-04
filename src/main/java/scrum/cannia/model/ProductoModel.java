package scrum.cannia.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="Producto")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProductoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_producto")
    private int id;

    @Column(length = 20, nullable = false )
    private String nombre;

    @Column(length = 300, nullable = false )
    private String descripcion;

    @Column(length = 20, nullable = false )
    private Integer cantidad;

    @Column(length = 20, nullable = false )
    private Integer Valor;

    @Column(length = 20, nullable = false )
    private boolean estado;

    @Column(length = 20, nullable = false )
    private byte foto;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private UnidadMedida unidadMedida;

    @OneToMany(mappedBy = "producto")
    private List<InventarioModel> inventarios = new ArrayList<>();

}
