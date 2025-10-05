package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.VeterinarioModel;

//Pondemos usar las funciones del jpa... como guardar
public interface VeterinarioRepository extends JpaRepository<VeterinarioModel,Long> {

}