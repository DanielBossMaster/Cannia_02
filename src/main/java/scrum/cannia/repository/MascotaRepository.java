package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.TipoEstadoMascota;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<MascotaModel, Long> {
    List<MascotaModel> findByPropietarioId(Long idPropietario);

    List<MascotaModel> findByTipoEstado(TipoEstadoMascota tipo);

    Optional<MascotaModel> findByIdAndPropietario_Id(Long mascotaId, Long propietarioId);

    List<MascotaModel> findByFundacion(FundacionModel fundacion);

    List<MascotaModel> findByEstadoAdopcion(String estado);

    Optional<MascotaModel> findById(Long id);

    @Query("""
    SELECT DISTINCT m
    FROM MascotaModel m
    LEFT JOIN FETCH m.historiasClinicas
    WHERE m.propietario = :propietario
""")
    List<MascotaModel> findByPropietarioConHistoria(
            @Param("propietario") PropietarioModel propietario
    );
    }
