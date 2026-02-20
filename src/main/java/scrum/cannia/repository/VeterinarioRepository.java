package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.VeterinarioModel;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<VeterinarioModel,Long> {

    Optional<VeterinarioModel> findByUsuarioUsuario(String usuario);
}