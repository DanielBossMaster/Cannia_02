package scrum.cannia.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.SolicitudAdopcionModel;

import java.util.List;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcionModel, Long> {

    List<SolicitudAdopcionModel> findByMascota(MascotaModel mascota);
    @Query("""
        SELECT s
        FROM SolicitudAdopcionModel s
        JOIN s.mascota m
        WHERE m.fundacion = :fundacion
    """)
    List<SolicitudAdopcionModel> findByFundacion(@Param("fundacion") FundacionModel fundacion);


}


