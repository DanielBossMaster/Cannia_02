package scrum.cannia.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.FundacionModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.model.VeterinarioModel;
import scrum.cannia.repository.FundacionRepository;


@AllArgsConstructor
@Service
public class FundacionService {

    private final FundacionRepository fundacionRepository;

    public FundacionModel buscarPorUsuario(UsuarioModel usuario){

        return fundacionRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Fundación no encontrada"));

    }
    public FundacionModel buscarUsuario(String username) {
        return fundacionRepository.findByUsuarioUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Fundacion no encontrado"));
    }

    public FundacionModel actualizarCampo(
            String username,
            String campo,
            String valor
    ) {

        FundacionModel fundacion =
                buscarUsuario(username);

        switch (campo) {
            case "nit":
                fundacion.setNit(valor);
                break;
            case "nombre":
                fundacion.setNombre(valor);
                break;
            case "descripcion":
                fundacion.setDescripcion(valor);
                break;
            case "direccion":
                fundacion.setDireccion(valor);
                break;
            case "telefono":
                fundacion.setTelefono(valor);
                break;
            case "email":
                fundacion.setEmail(valor);
                break;
            default:
                throw new IllegalArgumentException("Campo no válido");
        }
        return fundacionRepository.save(fundacion);
    }
}

