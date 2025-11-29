package scrum.cannia.repository;

import scrum.cannia.model.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Integer> {
    List<ProductoModel> findByEstadoTrue(); // Solo productos activos
    List<ProductoModel> findByPublicadoTrue(); // Solo productos publicados
}