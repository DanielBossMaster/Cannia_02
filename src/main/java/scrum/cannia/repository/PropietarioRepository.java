package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.PropietarioModel;

public interface PropietarioRepository extends JpaRepository<PropietarioModel,Long> {
}
