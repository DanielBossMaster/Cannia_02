package scrum.cannia.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.repository.ServicioRepository;

import java.util.List;
@AllArgsConstructor
@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public List<ServicioModel> listarTodos() {
        return servicioRepository.findAll();
    }

    // ============================================
    //         EDITAR SERVICIO
    // ============================================
    public void actualizar(ServicioModel servicio) {
        ServicioModel original = servicioRepository.findById(servicio.getId()).orElse(null);
        if (original == null) return;

        original.setNombre(servicio.getNombre());
        original.setDescripcion(servicio.getDescripcion());
        original.setDuracionEstimada(servicio.getDuracionEstimada());
        original.setPrecio(servicio.getPrecio());
        original.setEstado(servicio.isEstado());

        servicioRepository.save(original);
    }

    public ServicioModel buscarPorId(Integer id) {
        return servicioRepository.findById(id).orElse(null);
    }

    public Page<ServicioModel> listarPaginado(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return servicioRepository.findAll(pageable);

    }
    public Page<ServicioModel> listarActivoPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return servicioRepository.findByEstadoTrue(pageable);
    }
    public List<ServicioModel> listarTodosActivos() {
        return servicioRepository.findByEstadoTrue();
    }
    public List<ServicioModel> listarActivosPorVeterinaria(Integer idVeterinaria) {
        return servicioRepository.findByVeterinariaIdAndEstadoTrue(idVeterinaria);
    }

}
