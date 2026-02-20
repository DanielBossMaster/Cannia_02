package scrum.cannia.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<ProductoModel> findByEstadoTrue();
    Page<ProductoModel> findByEstadoTrue(
            Pageable pageable
    );
    Optional<ProductoModel> findByIdAndVeterinaria_Id(
            int id,
            Integer veterinariaId
    );
    Page<ProductoModel> findByVeterinaria_Id(
            Integer veterinariaId,
            Pageable pageable
    );
    List<ProductoModel> findByEstado(
            boolean estado
    );

    // =================================================================================
    //    MÉTODOS REQUERIDOS PARA FILTRAR POR Q, CATEGORÍA Y ESTADO=TRUE
    // =================================================================================

    @Modifying
    @Query(value = "DELETE FROM producto_categoria WHERE id_categoria = :categoriaId",
            nativeQuery = true)
    void desasociarProductosDeCategoria(@Param("categoriaId") Long categoriaId);

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

    @Query("""
    SELECT p FROM ProductoModel p
    WHERE p.estado = true
      AND p.veterinaria.id = :veterinariaId
""")
    List<ProductoModel> findActivosPorVeterinaria(
            @Param("veterinariaId") Integer veterinariaId
    );

    // Texto + estado + veterinaria
    @Query("""
    SELECT p FROM ProductoModel p
    WHERE p.estado = true
      AND p.veterinaria.id = :veterinariaId
      AND (
           UPPER(p.nombre) LIKE UPPER(CONCAT('%', :q, '%'))
        OR UPPER(p.descripcion) LIKE UPPER(CONCAT('%', :q, '%'))
      )
""")
    List<ProductoModel> findActivosPorVeterinariaYTexto(
            @Param("veterinariaId") Integer veterinariaId,
            @Param("q") String q
    );

    // Categoría + estado + veterinaria
    @Query("""
    SELECT p FROM ProductoModel p
    JOIN p.categorias c
    WHERE p.estado = true
      AND p.veterinaria.id = :veterinariaId
      AND c.id = :idCategoria
""")
    List<ProductoModel> findActivosPorVeterinariaYCategoria(
            @Param("veterinariaId") Integer veterinariaId,
            @Param("idCategoria") Long idCategoria
    );

    // Texto + categoría + estado + veterinaria
    @Query("""
    SELECT p FROM ProductoModel p
    JOIN p.categorias c
    WHERE p.estado = true
      AND p.veterinaria.id = :veterinariaId
      AND c.id = :idCategoria
      AND (
           UPPER(p.nombre) LIKE UPPER(CONCAT('%', :q, '%'))
        OR UPPER(p.descripcion) LIKE UPPER(CONCAT('%', :q, '%'))
      )
""")
    List<ProductoModel> findActivosPorVeterinariaTextoYCategoria(
            @Param("veterinariaId") Integer veterinariaId,
            @Param("q") String q,
            @Param("idCategoria") Long idCategoria
    );



}


