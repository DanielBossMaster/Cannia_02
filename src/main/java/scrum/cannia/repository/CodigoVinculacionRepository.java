package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.CodigoVinculacionModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodigoVinculacionRepository
        extends JpaRepository<CodigoVinculacionModel, Long> {

    Optional<CodigoVinculacionModel> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<CodigoVinculacionModel> findByPropietarioIdAndUsadoFalse(Long propietarioId);
}