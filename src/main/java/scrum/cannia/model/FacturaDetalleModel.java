package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "factura_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDetalleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_inventario")
    private InventarioModel inventario;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private FacturaModel factura;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoModel producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precio; // precio unitario * cantidad
}
