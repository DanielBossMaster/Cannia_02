package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.ServicioModel;
import scrum.cannia.repository.ServicioRepository;

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
}
//
//    public ServicioModel guardarServicio(ServicioModel servicio) {
//        return servicioRepository.save(servicio);
//    }
//
//    public Optional<ServicioModel> obtenerServicioPorId(Integer id) {
//        return servicioRepository.findById(id);
//    }
//
//    public void eliminarServicioLogicamente(Integer id) {
//        servicioRepository.deleteById(id);
//    }
//    public ServicioModel buscarPorId(Integer id) {
//        return servicioRepository.findById(id).orElse(null);
//    }
//
//    public List<ServicioModel> listarTodos() {
//        return servicioRepository.findAll();
//    }
