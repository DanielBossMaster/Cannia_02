package scrum.cannia.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scrum.cannia.model.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Integer> {

    @Modifying
    @Query(value = "DELETE FROM producto_categoria WHERE id_categoria = :categoriaId",
            nativeQuery = true)
        // El parámetro debe ser Long, que es el tipo del ID de la categoría
    void desasociarProductosDeCategoria(@Param("categoriaId") Long categoriaId);

    List<ProductoModel> findByEstadoTrue(); // Solo productos activos

    /// Métodos automáticos de Spring Data JPA
    List<ProductoModel> findByEstado(boolean estado);

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

    // =================================================================================
    // MÉTODOS REQUERIDOS PARA FILTRAR POR Q, CATEGORÍA Y ESTADO=TRUE
    // =================================================================================

    // 1. Buscar por Nombre/Descripción (q) Y Estado=True
    @Query("SELECT p FROM ProductoModel p " +
            "WHERE (UPPER(p.nombre) LIKE UPPER(CONCAT('%', :q, '%')) OR UPPER(p.descripcion) LIKE UPPER(CONCAT('%', :q, '%'))) " +
            "AND p.estado = TRUE")
    List<ProductoModel> findByNombreOrDescripcionContainingAndEstadoTrue(@Param("q") String q);


    // 2. Buscar por Categoría Y Estado=True
    @Query("SELECT p FROM ProductoModel p JOIN p.categorias c WHERE c.id = :idCategoria AND p.estado = TRUE")
    List<ProductoModel> findByCategoriaIdAndEstadoTrue(@Param("idCategoria") Long idCategoria);


    // 3. Buscar por Nombre/Descripción (q) Y Categoría Y Estado=True
    @Query("SELECT p FROM ProductoModel p JOIN p.categorias c " +
            "WHERE (UPPER(p.nombre) LIKE UPPER(CONCAT('%', :q, '%')) OR UPPER(p.descripcion) LIKE UPPER(CONCAT('%', :q, '%'))) " +
            "AND c.id = :idCategoria " +
            "AND p.estado = TRUE")
    List<ProductoModel> findByNombreOrDescripcionAndCategoriaIdAndEstadoTrue(@Param("q") String q, @Param("idCategoria") Long idCategoria);

}


