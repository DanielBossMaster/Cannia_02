package scrum.cannia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.PasswordResetTokenModel;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetTokenModel, Long> {

    Optional<PasswordResetTokenModel> findByToken(String token);

}