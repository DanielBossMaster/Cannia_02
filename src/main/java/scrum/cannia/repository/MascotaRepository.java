package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.TipoEstadoMascota;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<MascotaModel, Long> {
    List<MascotaModel> findByPropietarioId(Long idPropietario);

    List<MascotaModel> findByPropietario(PropietarioModel propietario);

    List<MascotaModel> findByFundacion_Id(Long fundacionId);

    List<MascotaModel> findByTipoEstado(TipoEstadoMascota tipo);

    // adopcion
//    List<MascotaModel> findByFundacionIsNotNullAndEstadoTrue(); // mascotas en  adopción (activas)
//    List<MascotaModel> findByFundacionIsNotNullAndEstadoTrueAndEspecie(String especie);// filtro busqueda por especie
}