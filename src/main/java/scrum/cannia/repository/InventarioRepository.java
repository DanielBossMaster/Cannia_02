package scrum.cannia.repository;

import scrum.cannia.model.InventarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioModel, Integer> {

    // Para alertas de stock
    List<InventarioModel> findByStockActualLessThan(Integer stockMinimo);
    List<InventarioModel> findByStockActualBetween(Integer min, Integer max);

    // Para gráficos
    @Query("SELECT i FROM InventarioModel i WHERE i.fechaActualizacion >= :fecha")
    List<InventarioModel> findInventariosRecientes(@Param("fecha") LocalDate fecha);


    Optional<InventarioModel> findByProductoIdAndVeterinariaId(int productoId, int veterinariaId);

    List<InventarioModel> findByVeterinariaId(Integer veterinariaId);
}