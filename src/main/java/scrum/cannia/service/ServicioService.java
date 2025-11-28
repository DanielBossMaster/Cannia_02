package scrum.cannia.service;

import scrum.cannia.model.ServicioModel;
import scrum.cannia.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    public List<ServicioModel> obtenerTodosServicios() {
        return servicioRepository.findAll();
    }

    public ServicioModel guardarServicio(ServicioModel servicio) {
        return servicioRepository.save(servicio);
    }

    public Optional<ServicioModel> obtenerServicioPorId(Integer id) {
        return servicioRepository.findById(id);
    }

    public void eliminarServicioLogicamente(Integer id) {
        servicioRepository.deleteById(id);
    }
    public ServicioModel buscarPorId(Integer id) {
        return servicioRepository.findById(id).orElse(null);
    }

    public List<ServicioModel> listarTodos() {
        return servicioRepository.findAll();
    }
}