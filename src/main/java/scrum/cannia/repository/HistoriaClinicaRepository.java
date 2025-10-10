package scrum.cannia.repository;

import scrum.cannia.model.HistoriaClinicaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinicaModel, Long> {

    // ★★★ MÉTODO PARA OBTENER HISTORIAS POR MASCOTA ORDENADAS POR FECHA ★★★
    List<HistoriaClinicaModel> findByMascotaIdOrderByFechaHoraDesc(Long mascotaId);

    // ★★★ MÉTODO PARA OBTENER HISTORIAS POR MASCOTA Y RANGO DE FECHAS ★★★
    List<HistoriaClinicaModel> findByMascotaIdAndFechaHoraBetweenOrderByFechaHoraDesc(
            @Param("mascotaId") Long mascotaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // ★★★ VERSIÓN ALTERNATIVA CON @Query (si la anterior no funciona) ★★★
    @Query("SELECT h FROM HistoriaClinicaModel h WHERE h.mascota.id = :mascotaId AND h.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaHora DESC")
    List<HistoriaClinicaModel> findHistoriasByMascotaAndFechaRange(
            @Param("mascotaId") Long mascotaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}