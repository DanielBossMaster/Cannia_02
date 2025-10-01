package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.VacunaModel;

@Repository
public interface VacunaRepository extends JpaRepository<VacunaModel, Long> {}