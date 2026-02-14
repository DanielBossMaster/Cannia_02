package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.VeterinarioModel;
import scrum.cannia.repository.PropietarioRepository;

import java.util.List;

@Service
public class PropietarioService {

    @Autowired
    private PropietarioRepository propietarioRepository;

    // ============================
    // ELIMINADO LÓGICO
    // ============================
    public void eliminarPropietario(Long id) {
        PropietarioModel propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        propietario.setEstado(false);
        propietarioRepository.save(propietario);
    }

    // ============================
    // OBTENER POR ID
    // ============================
    public PropietarioModel obtenerPorId(Long id) {
        return propietarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Propietario no encontrado con id: " + id));
    }

    // ============================
    // ⭐ NUEVO: LISTAR POR VETERINARIO
    // ============================
    public Page<PropietarioModel> listarPorVeterinario(
            VeterinarioModel veterinario,
            int pagina,
            int tamanio
    ) {
        return propietarioRepository.findByVeterinarioAndEstadoTrue(
                veterinario,
                PageRequest.of(pagina, tamanio)
        );
    }

    public List<PropietarioModel> listarPorVeterinario(
            VeterinarioModel veterinario

    ) {
        return propietarioRepository.findByVeterinarioAndEstadoTrue(
                veterinario

        );
    }


    public PropietarioModel obtenerPorIdYVeterinario(
            Long propietarioId,
            VeterinarioModel veterinario
    ) {
        return propietarioRepository
                .findByIdAndVeterinarioAndEstadoTrue(propietarioId, veterinario)
                .orElseThrow(() ->
                        new RuntimeException("Propietario no encontrado o no autorizado"));
    }
}