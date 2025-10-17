package scrum.cannia.service.VeterinarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.repository.PropietarioRepository;

import java.util.Optional;


@Service
public class PropietarioService {

    @Autowired
    private PropietarioRepository propietarioRepository;

    public void eliminarPropietario(Long id) {
        PropietarioModel propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        propietario.setEstado(false);
        propietarioRepository.save(propietario);
    }

    public PropietarioModel obtenerPorId(Long id) {
        return propietarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado con id: " + id));
    }

}