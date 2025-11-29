package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.PetModel;

public interface PetRepository extends JpaRepository<PetModel, Long> {
}
