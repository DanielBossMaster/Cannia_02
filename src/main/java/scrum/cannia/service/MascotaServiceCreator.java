package scrum.cannia.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.Dto.ErrorCargaDTO;
import scrum.cannia.Dto.ResultadoCargaMascotasDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.FundacionRepository;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.service.creator.MascotaCreator;
import scrum.cannia.service.creator.MascotaFundacionCreator;
import scrum.cannia.service.creator.MascotaPropietarioCreator;

import java.util.ArrayList;
import java.util.List;

@Service
public class MascotaServiceCreator {

//    private final MascotaServiceCreator mascotaServiceCreator;
    private final MascotaRepository mascotaRepository;
    private final FundacionRepository fundacionRepository;

    public MascotaServiceCreator(MascotaRepository mascotaRepository,
                                 FundacionRepository fundacionRepository) {
        this.mascotaRepository = mascotaRepository;
        this.fundacionRepository = fundacionRepository;
    }

    // 1️⃣ Crear y guardar una mascota desde PROPIETARIO
    @Transactional
    public MascotaModel crearDesdePropietario(
            String nombre,
            String especie,
            String raza,
            String color,
            Genero genero,
            PropietarioModel propietario
    ) {

        MascotaCreator creator = new MascotaPropietarioCreator(
                nombre, especie, raza, color, genero, propietario
        );

        MascotaModel mascota = creator.crearMascota();
        return mascotaRepository.save(mascota);
    }

    // 2️⃣ Crear y guardar una lista de mascotas desde FUNDACIÓN (para la carga Excel)
    @Transactional
    public ResultadoCargaMascotasDTO crearDesdeFundacion(
            List<MascotaCargaDTO> dtos,
            Long fundacionId
    ) {

        FundacionModel fundacion = fundacionRepository.findById(fundacionId)
                .orElseThrow(() -> new RuntimeException("Fundación no encontrada"));

        List<MascotaModel> guardadas = new ArrayList<>();
        List<ErrorCargaDTO> errores = new ArrayList<>();

        int fila = 1;

        for (MascotaCargaDTO dto : dtos) {
            fila++;
            try {
                MascotaCreator creator = new MascotaFundacionCreator(dto, fundacion);
                MascotaModel mascota = creator.crearMascota();
                mascotaRepository.save(mascota);
                guardadas.add(mascota);
            } catch (Exception e) {
                errores.add(new ErrorCargaDTO(fila, e.getMessage()));
            }
        }

        return new ResultadoCargaMascotasDTO(guardadas, errores);
    }
}

