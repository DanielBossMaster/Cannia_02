package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.AgendaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.VeterinariaModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendaRepository  extends JpaRepository<AgendaModel, Integer> {

    boolean existsByServicioAndFechaAndHoraAndEstado(
            ServicioModel servicio,
            LocalDate fecha,
            LocalTime hora,
            String estado
    );

    List<AgendaModel> findByVeterinariaOrderByFechaDescHoraDesc(
            VeterinariaModel veterinaria
    );

    List<AgendaModel> findByPropietario(PropietarioModel propietario);

    // opcional: solo activas
    List<AgendaModel> findByPropietarioAndEstado(
            PropietarioModel propietario,
            String estado
    );
}
