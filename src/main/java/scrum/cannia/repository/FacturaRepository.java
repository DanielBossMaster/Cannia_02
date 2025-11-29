package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import scrum.cannia.model.FacturaModel;

public interface FacturaRepository  extends JpaRepository<FacturaModel, Long> {
}
