package scrum.cannia.repository;

import scrum.cannia.model.ServicioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioModel, Integer> {
    List<ServicioModel> findAllByOrderByNombreAsc();
}