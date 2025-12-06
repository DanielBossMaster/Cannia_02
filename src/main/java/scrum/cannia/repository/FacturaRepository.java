package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import scrum.cannia.model.FacturaModel;

import java.util.List;

public interface FacturaRepository  extends JpaRepository<FacturaModel, Long> {
    List<FacturaModel> findByVeterinaria_IdOrderByFechaEmisionDesc(Integer veterinariaId);

}
