package scrum.cannia.repository;
import scrum.cannia.model.FundacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scrum.cannia.model.UsuarioModel;

import java.util.Optional;

@Repository
public interface FundacionRepository extends JpaRepository<FundacionModel, Long> {
    Optional<FundacionModel> findByUsuario(UsuarioModel usuario);
}

