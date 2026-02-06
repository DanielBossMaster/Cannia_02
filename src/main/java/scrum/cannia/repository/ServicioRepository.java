package scrum.cannia.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.ServicioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioModel, Integer> {

    Page<ServicioModel> findByEstadoTrue(Pageable pageable);
    List<ServicioModel> findByEstadoTrue();
    List<ServicioModel> findByVeterinariaIdAndEstadoTrue(Integer idVeterinaria);

}