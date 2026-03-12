package scrum.cannia.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.FundacionRepository;


@AllArgsConstructor
@Service
public class FundacionService {

    private final FundacionRepository fundacionRepository;

    public FundacionModel buscarPorUsuario(UsuarioModel usuario){

        return fundacionRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Fundación no encontrada"));

    }
}

