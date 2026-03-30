package scrum.cannia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import scrum.cannia.model.MascotaModel;

import java.time.LocalDate;
@Entity
@Table(name = "solicitud_adopcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAdopcionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mascota solicitada
    @ManyToOne
    @JoinColumn(name = "id_mascota")
    private MascotaModel mascota;

    // Usuario registrado (opcional)
    @ManyToOne
    @JoinColumn(name = "id_propietario")
    private PropietarioModel propietario;

    // Datos visitante
    private String nombre;
    private String email;
    private String telefono;

    private String experiencia;
    private String vivienda;
    private String motivo;
    private String mascotas;

    private String estado; // PENDIENTE, ACEPTADA, RECHAZADA

    private LocalDate fechaSolicitud;
}
