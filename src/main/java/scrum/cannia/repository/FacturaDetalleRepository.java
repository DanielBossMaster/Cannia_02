package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.FacturaDetalleModel;


public interface FacturaDetalleRepository extends JpaRepository<FacturaDetalleModel, Long>{}




