package scrum.cannia.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.model.VeterinarioModel;

import java.util.List;
import java.util.Optional;

public interface PropietarioRepository extends JpaRepository<PropietarioModel,Long> {

    List<PropietarioModel> findByVeterinarioAndEstadoTrue(VeterinarioModel veterinario);
    Page<PropietarioModel> findByVeterinarioAndEstadoTrue(VeterinarioModel veterinario,Pageable pageable);
    Optional<PropietarioModel> findByIdAndVeterinarioAndEstadoTrue(
            Long id,
            VeterinarioModel veterinario);



    @Query("SELECT p.correoPro FROM PropietarioModel p WHERE p.correoPro IS NOT NULL")
    List<String> obtenerCorreosDePropietarios();
}

