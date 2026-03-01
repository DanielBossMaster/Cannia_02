package scrum.cannia.repository;

import scrum.cannia.model.VacunaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VacunaRepository extends JpaRepository<VacunaModel, Long> {

    // ★★★ MÉTODO PARA OBTENER VACUNAS POR MASCOTA ★★★
    List<VacunaModel> findByMascotaId(Long mascotaId);

    // ★★★ VERSIÓN ALTERNATIVA CON @Query ★★★
    @Query("SELECT v FROM VacunaModel v WHERE v.mascota.id = :mascotaId ORDER BY v.fechaAplicacion DESC")
    List<VacunaModel> findVacunasByMascotaIdOrderByFechaAplicacionDesc(@Param("mascotaId") Long mascotaId);
}