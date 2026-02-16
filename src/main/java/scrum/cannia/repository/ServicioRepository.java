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

    Page<ServicioModel> findByVeterinaria_IdAndEstadoTrue(
            Integer veterinariaId,
            Pageable pageable
    );

    List<ServicioModel> findByVeterinaria_IdAndEstadoTrue(
            Integer veterinariaId);

    ServicioModel findByIdAndVeterinaria_Id(
            Integer id,
            Integer veterinariaId);

    Page<ServicioModel> findByVeterinariaIdAndEstadoTrue(
            Integer veterinariaId,
            Pageable pageable
    );
}