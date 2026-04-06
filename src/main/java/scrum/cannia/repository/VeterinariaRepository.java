package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.AgendaModel;
import scrum.cannia.model.VeterinariaModel;

import java.util.List;

public interface VeterinariaRepository  extends JpaRepository<VeterinariaModel,Integer> {
}
