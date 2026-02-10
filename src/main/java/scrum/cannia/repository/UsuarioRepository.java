package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.UsuarioModel;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    UsuarioModel findByUsuarioAndContrasena(String usuario, String contrasena);

    Optional<UsuarioModel> findByUsuario(String usuario);

    boolean existsByRol(String rol);

}
