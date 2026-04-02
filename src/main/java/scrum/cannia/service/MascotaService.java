package scrum.cannia.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
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
import java.util.Base64;
import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;


    @Transactional
    public void eliminarMascotaLogico(Long idMascota){

        MascotaModel mascota = mascotaRepository
                .findById(idMascota)
                .orElseThrow(() ->
                        new RuntimeException("Mascota no encontrada"));

        mascota.setActivo(false);

        mascotaRepository.save(mascota);
    }

    public List<MascotaModel> listarPorPropietario(PropietarioModel propietario) {

        return mascotaRepository
                .findByPropietarioIdAndActivoTrue(propietario.getId());
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

    @Transactional
    public void registrarMascotaFundacion(MascotaModel mascota, FundacionModel fundacion){

        if (fundacion == null) {
            throw new IllegalStateException("Fundación no válida");
        }
        mascota.setFundacion(fundacion);
        mascota.setEstadoAdopcion("DISPONIBLE");
        fundacion.getMascotas().add(mascota);
        mascotaRepository.save(mascota);
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

    public List<MascotaModel> listarConHistoriaYVacunas(
            PropietarioModel propietario) {
        return mascotaRepository.findByPropietarioConHistoriaActivas(propietario);
    }


    public MascotaModel buscarPorId(Long id){

        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
    }

    public void actualizarMascota(MascotaModel mascota){

        MascotaModel existente = mascotaRepository
                .findById(mascota.getId())
                .orElseThrow();

        existente.setNomMascota(mascota.getNomMascota());
        existente.setEspecie(mascota.getEspecie());
        existente.setRaza(mascota.getRaza());
        existente.setEstadoAdopcion(mascota.getEstadoAdopcion());
        existente.setDescripcion(mascota.getDescripcion());

        if(mascota.getFoto() != null){
            existente.setFoto(mascota.getFoto());
        }

        mascotaRepository.save(existente);
    }


    public List<MascotaModel> obtenerMascotasFundacion(FundacionModel fundacion){

        List<MascotaModel> mascotas = mascotaRepository.findByFundacion(fundacion);

        return mascotas;
    }

    public List<MascotaModel> obtenerMascotasDisponibles(){

        List<MascotaModel> mascotas =
                mascotaRepository.findByEstadoAdopcion("DISPONIBLE");


        return mascotas;
    }
}






