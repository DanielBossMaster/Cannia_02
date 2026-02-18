package scrum.cannia.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.model.VeterinariaModel;
import scrum.cannia.repository.ServicioRepository;

import java.util.List;
@AllArgsConstructor
@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    // ============================================
    //      GUARDAR SERVICIO POR VETERINARIA
    // ============================================

    public void guardarServicioVeterinaria(
            ServicioModel servicio,
            VeterinariaModel veterinaria
    ) {

        servicio.setVeterinaria(veterinaria);
        servicio.setEstado(true); // activo por defecto

        servicioRepository.save(servicio);
    }

    // ============================================
    //      LISTAR SERVICIOS POR VETERINARIA
    // ============================================

    public Page<ServicioModel> listarActivosPorVeterinaria(
            Integer veterinariaId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return servicioRepository
                .findByVeterinariaIdAndEstadoTrue(veterinariaId, pageable);
    }
    public Page<ServicioModel>listarTodosPorVeterinaria(
            Integer veterinariaId,
            int page,
            int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        return servicioRepository
                .findByVeterinaria_Id(veterinariaId, pageable);
    }



    public List<ServicioModel> listarActivosPorVeterinaria(Integer veterinariaId) {
        return servicioRepository.findByVeterinaria_IdAndEstadoTrue(veterinariaId);
    }

    // ============================================
    //      EDITAR SERVICIO (CON SEGURIDAD)
    // ============================================

    public ServicioModel obtenerServicioVeterinaria(
            Integer servicioId,
            Integer veterinariaId
    ) {
        ServicioModel servicio = servicioRepository
                .findByIdAndVeterinaria_Id(servicioId, veterinariaId);

        if (servicio == null) {
            throw new RuntimeException("Servicio no autorizado");
        }

        return servicio;
    }

    public void actualizarServicioVeterinaria(
            ServicioModel cambios,
            Integer veterinariaId
    ) {

        ServicioModel original = obtenerServicioVeterinaria(
                cambios.getId(),
                veterinariaId
        );

        original.setNombre(cambios.getNombre());
        original.setDescripcion(cambios.getDescripcion());
        original.setDuracionEstimada(cambios.getDuracionEstimada());
        original.setPrecio(cambios.getPrecio());
        original.setEstado(cambios.isEstado());

        servicioRepository.save(original);
    }
}