package scrum.cannia.service;

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
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final FundacionRepository fundacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public MascotaService(MascotaRepository mascotaRepository,
                          FundacionRepository fundacionRepository,
                          UsuarioRepository usuarioRepository) {
        this.mascotaRepository = mascotaRepository;
        this.fundacionRepository = fundacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ResultadoCargaMascotasDTO guardarMascotasDesdeFundacion(
            List<MascotaCargaDTO> lista, Long fundacionId) {

        FundacionModel fundacion = fundacionRepository.findById(fundacionId)
                .orElseThrow(() -> new RuntimeException("Fundación no encontrada."));

        List<MascotaModel> guardadas = new ArrayList<>();
        List<ErrorCargaDTO> errores = new ArrayList<>();

        int fila = 1;

        for (MascotaCargaDTO dto : lista) {
            fila++;

            try {
                MascotaModel mascota = MascotaModel.crearDesdeFundacion(dto, fundacion);

                mascotaRepository.save(mascota);
                guardadas.add(mascota);

            } catch (Exception e) {
                errores.add(new ErrorCargaDTO(fila, e.getMessage()));
            }
        }

        return new ResultadoCargaMascotasDTO(guardadas, errores);
    }


    public List<MascotaModel> listarMascotasDisponibles() {
        return mascotaRepository.findByTipoEstado(TipoEstadoMascota.DISPONIBLE);
    }
//---------------------------------------------------------------------------------------

//     🔹 Listar todas las mascotas
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

//    metodos para el crdu de mascotas en fundaciones

//     listar mascotas de una fundación
    public List<MascotaModel> listarPorFundacion(Long fundacionId) {
        return mascotaRepository.findByFundacion_Id(fundacionId);
    }

//     guardar  nueva mascota asociada a una fundación
    public MascotaModel guardarEnFundacion(Long fundacionId, MascotaModel mascota) {
        FundacionModel f = fundacionRepository.findById(fundacionId)
                .orElseThrow(() -> new RuntimeException("La Fundación no existe: " + fundacionId));

        mascota.setPropietario(null);     //  si la mascota está en fundación no tiene propietario
        mascota.setFundacion(f);

        return mascotaRepository.save(mascota);
    }

//    actualizar mascota en una fundación
    public MascotaModel actualizarMascota(Long id, MascotaModel mascota) {
        MascotaModel existente = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        existente.setNomMascota(mascota.getNomMascota());
        existente.setEspecie(mascota.getEspecie());
        existente.setRaza(mascota.getRaza());
        existente.setFechaNacimiento(mascota.getFechaNacimiento());
        existente.setFechaVacunacion(mascota.getFechaVacunacion());
        existente.setMedicamento(mascota.getMedicamento());
        existente.setColor(mascota.getColor());
        existente.setFoto(mascota.getFoto());
        existente.setGenero(mascota.getGenero());
        existente.setEdadFundacion(mascota.getEdadFundacion());

        // No permitimos moverla a propietario o cambiarlo a otra fundación aquí
        return mascotaRepository.save(existente);
    }

//     metodo para la vista de adopción del Propietario
//    public List<MascotaModel> listarMascotasEnAdopcionPorEspecie(String especie) {
//        if (especie == null || especie.isEmpty() || especie.equalsIgnoreCase("Todos")) {
//            return mascotaRepository.findByFundacionIsNotNullAndEstadoTrue(); // Lista todas las activas en fundación
//        }
//
//        return mascotaRepository.findByFundacionIsNotNullAndEstadoTrueAndEspecie(especie);
//    }

}