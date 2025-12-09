// src/main/java/scrum/cannia/repository/BusquedaRepository.java
package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scrum.cannia.model.ProductoModel;
import java.util.List;

public interface BusquedaRepository extends JpaRepository<ProductoModel, Integer> {

    /**
     * Usa JPQL para buscar productos que coincidan con el texto 'query' y el filtro de 'idCategoria'.
     * * JPQL: Hace un JOIN implícito a través de la relación 'categorias' definida en ProductoModel.
     */
    @Query("SELECT p FROM ProductoModel p JOIN p.categorias c WHERE c.id = :idCategoria AND p.nombre LIKE %:query%")
    List<ProductoModel> buscarProductosPorFiltros(
            @Param("idCategoria") Long idCategoria,
            @Param("query") String query
    );

    /**
     * Método para la búsqueda general (cuando no hay filtro de categoría).
     */
    List<ProductoModel> findByNombreContainingIgnoreCase(String nombre);
}