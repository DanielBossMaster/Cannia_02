package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import scrum.cannia.model.FacturaModel;
import scrum.cannia.model.ProductoModel;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository  extends JpaRepository<FacturaModel, Long> {
    List<FacturaModel> findByVeterinaria_IdOrderByFechaEmisionDesc(Integer veterinariaId);
    Optional<FacturaModel> findByIdAndVeterinaria_Id(Long id, Integer veterinariaId);

}
