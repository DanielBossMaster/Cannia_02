package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<MascotaModel, Long> {
    List<MascotaModel> findByPropietarioId(Long idPropietario);

    List<MascotaModel> findByPropietario(PropietarioModel propietario);
}
