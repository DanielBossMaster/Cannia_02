package scrum.cannia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.repository.MascotaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    // 🔹 Listar todas las mascotas
    public List<MascotaModel> listarTodas() {
        return mascotaRepository.findAll();
    }

    // 🔹 Guardar o actualizar una mascota
    public void guardar(MascotaModel mascota) {
        mascotaRepository.save(mascota);
    }

    // 🔹 Buscar una mascota por su ID
    public Optional<MascotaModel> obtenerPorId(Long id) {
        return mascotaRepository.findById(id);
    }



    // 🔹 (Opcional) Listar mascotas de un propietario específico
    public List<MascotaModel> listarPorPropietario(Long propietarioId) {
        return mascotaRepository.findByPropietarioId(propietarioId);
    }
}
