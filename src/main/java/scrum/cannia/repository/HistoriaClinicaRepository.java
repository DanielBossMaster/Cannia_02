package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.HistoriaClinicaModel;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinicaModel, Long> {}

