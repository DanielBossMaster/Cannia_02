package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Long id;

    @Column(name= "fecha_emision", length = 100)
    private LocalDateTime fechaEmision;

    @Column(length = 100)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private  MetodoPago metodoPago;

    @Column(length = 40, nullable = false)
    private BigDecimal precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoFactura estado;

    @ManyToOne
    @JoinColumn(name = "id_veterinaria",nullable = false)
    private VeterinariaModel veterinaria;

    @ManyToOne
    @JoinColumn(name = "id_propietario",nullable = false)
    private PropietarioModel propietario;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaDetalleModel> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fechaEmision = LocalDateTime.now();
        this.estado = EstadoFactura.PAGADA;
    }



}
