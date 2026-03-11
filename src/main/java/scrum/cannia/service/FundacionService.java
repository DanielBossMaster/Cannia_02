package scrum.cannia.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.repository.FundacionRepository;


@AllArgsConstructor
@Service
public class FundacionService {

    private final FundacionRepository fundacionRepository;

    public FundacionModel buscarPorId(Long id){

        return fundacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fundación no encontrada"));
    }

    public FundacionModel buscarPorCorreo(String correo){

        return fundacionRepository.findByEmail(correo)
                .orElseThrow(() -> new RuntimeException("Fundación no encontrada"));

    }
}

