package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class InventarioModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private Integer stockActual;

    @Column(length = 50, nullable = false)
    private Integer stockMinimo; // Es para hacer una recomendacion oa recordatoriao de qeu tiene bajo una producto

    @Column(length = 50, nullable = false)
    private Integer stockMaximo;// Tambien para recordatorio de no excederse

    @Column(length = 50, nullable = false)
    private Integer PrecioCompra;

    @Column(length = 50, nullable = false)
    private Integer PrecioVenta;

    @Column(length = 50, nullable = false)
    private LocalDate fechaIngreso;

    @Column(length = 50, nullable = false)
    private LocalDate fechaVencimiento;

    @Column(length = 50, nullable = false)
    private LocalDate fechaActualizacion;

    @Column(length = 50, nullable = false)
    private long lote;

    @Column(length = 50, nullable = false)
    private UnidadMedida unidadMedida;

    @Column(length = 75, nullable = false)
    private String Provedor;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private ProductoModel producto;

    @ManyToOne
    @JoinColumn(name = "veterinaria_id", nullable = false)
    private VeterinariaModel veterinaria;

    @OneToMany(mappedBy = "inventario")
    private List<FacturaDetalleModel> facturaDetalles = new ArrayList<>();


}
