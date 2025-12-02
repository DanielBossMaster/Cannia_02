package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import scrum.cannia.model.PropietarioModel;

import java.util.List;

public interface PropietarioRepository extends JpaRepository<PropietarioModel,Long> {
List<PropietarioModel> findByEstadoTrue();
    PropietarioModel findByNumDoc(String numDoc);
    PropietarioModel findByUsuario_IdUsuario(Long idUsuario);


    @Query("SELECT p.correoPro FROM PropietarioModel p WHERE p.correoPro IS NOT NULL")
    List<String> obtenerCorreosDePropietarios();
}

