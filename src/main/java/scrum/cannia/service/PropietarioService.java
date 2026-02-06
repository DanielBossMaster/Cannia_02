package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.repository.PropietarioRepository;


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

    public Page<PropietarioModel> listarPaginado(int pagina, int tamanio) {
        return propietarioRepository.findByEstadoTrue(
                PageRequest.of(pagina, tamanio)
        );
    }

}