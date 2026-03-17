package scrum.cannia.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import scrum.cannia.model.*;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.SolicitudAdopcionRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class SolicitudAdopcionService {

    private final SolicitudAdopcionRepository solicitudRepository;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;
    private final MascotaRepository mascotaRepository;

    @Transactional
    public void crearSolicitud(
            Long mascotaId,
            String experiencia,
            String vivienda,
            String motivo,
            String nombre,
            String email,
            String telefono,
            UserDetails user
    ){


    MascotaModel mascota = mascotaService.buscarPorId(mascotaId);

        SolicitudAdopcionModel solicitud = new SolicitudAdopcionModel();

        solicitud.setMascota(mascota);
        solicitud.setExperiencia(experiencia);
        solicitud.setVivienda(vivienda);
        solicitud.setMotivo(motivo);

        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setEstado("PENDIENTE");

        // ==============================
        // SI ES USUARIO REGISTRADO
        // ==============================

        if(user != null){

            UsuarioModel usuario =
                    usuarioService.buscarPorUsername(user.getUsername());

            PropietarioModel propietario = usuario.getPropietario();

            solicitud.setPropietario(propietario);

        }
        else{

            solicitud.setNombre(nombre);
            solicitud.setEmail(email);
            solicitud.setTelefono(telefono);

        }


        solicitudRepository.save(solicitud);

    }

    public List<SolicitudAdopcionModel> obtenerSolicitudesFundacion(FundacionModel fundacion){

        return solicitudRepository.findByFundacion(fundacion);

    }

    @Transactional
    public void aceptarSolicitud(Long solicitudId){

        SolicitudAdopcionModel solicitud =
                solicitudRepository.findById(solicitudId)
                        .orElseThrow();

        MascotaModel mascota = solicitud.getMascota();

        // 1️ aprobar solicitud seleccionada
        solicitud.setEstado("APROBADA");

        // 2️ cambiar estado mascota
        mascota.setEstadoAdopcion("ADOPTADO");

        // 3️ si el solicitante es usuario registrado
        if(solicitud.getPropietario() != null){

            mascota.setPropietario(solicitud.getPropietario());

        }

        mascotaRepository.save(mascota);
        solicitudRepository.save(solicitud);

        // 4️ buscar TODAS las solicitudes de esa mascota
        List<SolicitudAdopcionModel> solicitudes =
                solicitudRepository.findByMascota(mascota);

        // 5️ rechazar las demás
        for(SolicitudAdopcionModel s : solicitudes){

            if(!s.getId().equals(solicitudId)){

                s.setEstado("RECHAZADA");

                solicitudRepository.save(s);

            }
        }
    }

    @Transactional
    public void rechazarSolicitud(Long solicitudId){

        SolicitudAdopcionModel solicitud =
                solicitudRepository.findById(solicitudId)
                        .orElseThrow();

        solicitud.setEstado("RECHAZADA");

        solicitudRepository.save(solicitud);
    }
}

