package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.UsuarioModel;

import java.util.List;
import java.util.Optional;

public interface PropietarioRepository extends JpaRepository<PropietarioModel,Long> {
List<PropietarioModel> findByEstadoTrue();
    PropietarioModel findByNumDoc(String numDoc);
    Optional<PropietarioModel> findByUsuario(UsuarioModel usuario);


    @Query("SELECT p.correoPro FROM PropietarioModel p WHERE p.correoPro IS NOT NULL")
    List<String> obtenerCorreosDePropietarios();
}

