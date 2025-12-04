package scrum.cannia.repository;

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
}
//    List<ProductoModel> findByPublicadoTrue(); // Solo productos publicados

