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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private int id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] foto;

    @Column(length = 20, nullable = false)
    private String nombre;

    @Column(length = 300, nullable = false)
    private String descripcion;

    @Column(length = 20, nullable = false)
    private Integer cantidad;

    @Column(length = 20, nullable = false)
    private Integer valor;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private UnidadMedida unidadMedida;


    @Column(length = 20, nullable = false)
    private boolean estado;


    @Column
    private boolean publicado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinaria")
    private VeterinariaModel veterinaria;

    /* Metodo para convertir imagenes */
    @Transient
    private String fotoBase64;

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    // Si InventarioModel tiene un campo llamado 'producto':
    @OneToMany(mappedBy = "producto")
    private List<InventarioModel> inventarios;

    // ===============================================
    // CAMBIO REQUERIDO: RELACIÓN CON CATEGORIAS
    // ===============================================

    @ManyToMany
    @JoinTable(
            name = "producto_categoria", // Nombre de la tabla intermedia que usamos en el script SQL
            joinColumns = @JoinColumn(name = "id_producto"), // Columna FK de Producto en la tabla intermedia
            inverseJoinColumns = @JoinColumn(name = "id_categoria") // Columna FK de Categoria en la tabla intermedia
    )
    private List<CategoriaModel> categorias;

    // ===============================================

    // ... campos y métodos existentes (inventarios, getFotoBase64, etc.)

}
