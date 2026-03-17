package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.CitaModel;
import scrum.cannia.model.EstadoCita;
import scrum.cannia.model.VeterinarioModel;

import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<CitaModel, Long> {



    boolean existsByVacunaIdAndEstadoCitaIn(
            Long vacunaId,
            List<EstadoCita> estados
    );

    Optional<CitaModel> findTopByVacuna_IdOrderByIdDesc(Long vacunaId);

    List<CitaModel> findByEstadoCitaOrderByFechaCitaAscHoraCitaAsc(EstadoCita estado);

    List<CitaModel> findByEstadoCitaInAndVacuna_Mascota_Propietario_Veterinario(
            List<EstadoCita> estados,
            VeterinarioModel veterinario
    );

    List<CitaModel> findByEstadoCitaIn(List<EstadoCita> estados);
}