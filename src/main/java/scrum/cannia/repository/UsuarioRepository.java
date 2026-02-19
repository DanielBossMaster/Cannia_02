package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.UsuarioModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    UsuarioModel findByUsuarioAndContrasena(String usuario, String contrasena);

    Optional<UsuarioModel> findByUsuario(String usuario);

    boolean existsByRol(String rol);

    List<UsuarioModel> findByEstadoAndRolIn(
            String estado,
            List<String> roles
    );
    @Query("""
    SELECT u FROM UsuarioModel u
    LEFT JOIN FETCH u.veterinario
    LEFT JOIN FETCH u.fundacion
    WHERE u.estado = 'PENDIENTE'
""")
    List<UsuarioModel> findUsuariosPendientesConDatos();


}
