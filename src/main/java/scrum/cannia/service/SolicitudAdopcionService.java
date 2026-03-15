package scrum.cannia.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.SolicitudAdopcionModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.SolicitudAdopcionRepository;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class SolicitudAdopcionService {

    private final SolicitudAdopcionRepository solicitudRepository;
    private final MascotaService mascotaService;
    private final UsuarioService usuarioService;

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
}

