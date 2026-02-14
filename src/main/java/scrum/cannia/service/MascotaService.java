package scrum.cannia.service;

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
@AllArgsConstructor
@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final FundacionRepository fundacionRepository;
    private final UsuarioRepository usuarioRepository;


    public List<MascotaModel> listarMascotasDisponibles() {
        return mascotaRepository.findByTipoEstado(TipoEstadoMascota.DISPONIBLE);
    }


    // 🔹 Guardar o actualizar una mascota
    public void guardar(MascotaModel mascota) {
        mascotaRepository.save(mascota);
    }


    // 🔹 (Opcional) Listar mascotas de un propietario específico
    public List<MascotaModel> listarPorPropietario(Long propietarioId) {
        return mascotaRepository.findByPropietarioId(propietarioId);
    }

    public List<MascotaModel> listarPorPropietario(PropietarioModel propietario) {
        return mascotaRepository.findByPropietarioAndTipoEstadoTrue(propietario);
    }

}







