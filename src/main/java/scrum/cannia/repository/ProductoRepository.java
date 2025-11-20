package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.ProductoModel;

import java.util.List;

public interface ProductoRepository  extends JpaRepository<ProductoModel, Integer> {
    List<ProductoModel> findByPublicadoTrue();

}
