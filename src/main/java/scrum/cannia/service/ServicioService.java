package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.ProductoModel;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.repository.ServicioRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<ServicioModel>listarTodos() {
        return servicioRepository.findAll();
    }
    public static <Servicio> void guardar
            (Servicio servicio) {

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

}

