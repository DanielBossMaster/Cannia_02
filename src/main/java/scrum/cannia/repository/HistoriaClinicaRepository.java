package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.HistoriaClinicaModel;

import java.util.List; // Cambiar de Optional a List
import java.util.Optional;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinicaModel, Long> {
    // Cambiar este metodo para que devuelva LISTA en lugar de Optional
    List<HistoriaClinicaModel> findByMascotaIdOrderByFechaHoraDesc(Long mascotaId);

    // Mantener el existente si lo necesitas para otra cosa
    Optional<HistoriaClinicaModel> findByMascotaId(Long mascotaId);
}