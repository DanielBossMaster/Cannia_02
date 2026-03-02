package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.CitaModel;
import scrum.cannia.model.EstadoCita;

import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<CitaModel, Long> {

    List<CitaModel> findByMascotaId(Long mascotaId);

    boolean existsByVacunaIdAndEstadoIn(
            Long vacunaId,
            List<EstadoCita> estados
    );

    Optional<CitaModel> findTopByVacuna_IdOrderByIdDesc(Long vacunaId);
}