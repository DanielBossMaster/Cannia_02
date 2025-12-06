package scrum.cannia.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scrum.cannia.model.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Integer> {
    List<ProductoModel> findByEstadoTrue(); // Solo productos activos
    Optional<ProductoModel> findByNombre(String nombre);
    List<ProductoModel> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);

    /// Métodos automáticos de Spring Data JPA
    List<ProductoModel> findByEstado(boolean estado);
    List<ProductoModel> findByPublicado(boolean publicado);
    List<ProductoModel> findByEstadoAndPublicado(boolean estado, boolean publicado);

    // Consulta para el reporte (ajustada a TU modelo)
    @Query("SELECT p.nombre, SUM(p.cantidad), p.estado " +
            "FROM ProductoModel p " +  // ← Usa ProductoModel (nombre de la clase)
            "WHERE (:estado IS NULL OR p.estado = :estado) " +
            "GROUP BY p.nombre, p.estado " +
            "ORDER BY p.nombre")
    List<Object[]> findResumenProductos(@Param("estado") Boolean estado);

    // Otra consulta útil por categoría (si tienes categoría en tu modelo)
    @Query("SELECT p, COUNT(p) FROM ProductoModel p GROUP BY p.estado")
    List<Object[]> contarPorEstado();
}
//    List<ProductoModel> findByPublicadoTrue(); // Solo productos publicados

