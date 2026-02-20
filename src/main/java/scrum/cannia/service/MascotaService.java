package scrum.cannia.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scrum.cannia.Dto.ErrorCargaDTO;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.Dto.ResultadoCargaMascotasDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.FundacionRepository;
import scrum.cannia.repository.UsuarioRepository;

//import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    // Mascotas disponibles (adopción)
    public List<MascotaModel> listarMascotasDisponibles() {
        return mascotaRepository.findByTipoEstado(TipoEstadoMascota.DISPONIBLE);
    }

    // Registrar mascota (solo propietario en sesión)
    @Transactional
    public void registrarMascota(MascotaModel mascota, PropietarioModel propietario) {

        if (propietario == null) {
            throw new IllegalStateException("Propietario no válido");
        }

        mascota.setPropietario(propietario);
        mascotaRepository.save(mascota);
    }

    // Listar SOLO las mascotas del propietario en sesión
    public List<MascotaModel> listarPorPropietario(PropietarioModel propietario) {
        return mascotaRepository.findByPropietarioId(propietario.getId());
    }

    // Obtener mascota segura (para historia clínica)
    public MascotaModel obtenerMascotaPropietario(
            Long mascotaId,
            PropietarioModel propietario
    ) {
        return mascotaRepository
                .findByIdAndPropietario_Id(mascotaId, propietario.getId())
                .orElseThrow(() ->
                        new IllegalStateException("Mascota no autorizada"));
    }
}





