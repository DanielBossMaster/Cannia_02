package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.ProductoModel;

public interface ProductoRepository  extends JpaRepository<ProductoModel, Integer> {


}
