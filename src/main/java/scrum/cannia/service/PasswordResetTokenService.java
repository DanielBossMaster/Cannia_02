package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.PasswordResetTokenModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository repository;

    public PasswordResetTokenModel crearToken(
            UsuarioModel usuario,
            String token){

        PasswordResetTokenModel tokenModel =
                new PasswordResetTokenModel();

        tokenModel.setToken(token);

        tokenModel.setUsuario(usuario);

        tokenModel.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(30)
        );

        return repository.save(tokenModel);
    }

    public PasswordResetTokenModel buscarPorToken(
            String token){

        Optional<PasswordResetTokenModel> resultado =
                repository.findByToken(token);

        return resultado.orElse(null);
    }

    public void eliminar(PasswordResetTokenModel token){

        repository.delete(token);
    }
}