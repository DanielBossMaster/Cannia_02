package scrum.cannia.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scrum.cannia.Dto.RegistroDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PropietarioRepository propietarioRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final FundacionRepository fundacionRepository;
    private final VeterinariaRepository veterinariaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PropietarioRepository propietarioRepository,
            VeterinarioRepository veterinarioRepository,
            FundacionRepository fundacionRepository,
            VeterinariaRepository veterinariaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.propietarioRepository = propietarioRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.fundacionRepository = fundacionRepository;
        this.veterinariaRepository = veterinariaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    public void registrarUsuario(RegistroDTO registroDTO) {

        if (usuarioRepository.findByUsuario(registroDTO.getUsuario()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe, Debe elegir un nombre de usuario distinto");
        }


        // =====================
        // CREAR USUARIO BASE
        // =====================
        UsuarioModel usuario = new UsuarioModel();
        usuario.setUsuario(registroDTO.getUsuario());
        usuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena()));
        usuario.setRol(registroDTO.getRol().toUpperCase());

        // =====================
        // LOGICA SEGÚN ROL
        // =====================

        if ("PROPIETARIO".equalsIgnoreCase(registroDTO.getRol())) {

            usuario.setEstado("ACTIVO"); //  Propietario activo

            VeterinariaModel veterinaria = veterinariaRepository
                    .findById(registroDTO.getIdVeterinariaSeleccionada())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Veterinaria no válida"));

            PropietarioModel propietario = new PropietarioModel();
            propietario.setNumDoc(registroDTO.getNumDoc());
            propietario.setNombrePro(registroDTO.getNombrePro());
            propietario.setApellidoPro(registroDTO.getApellidoPro());
            propietario.setDireccionPro(registroDTO.getDireccionPro());
            propietario.setTelefonoPro(registroDTO.getTelefonoPro());
            propietario.setCorreoPro(registroDTO.getCorreoPro());
            propietario.setVeterinaria(veterinaria);
            propietario.setUsuario(usuario);

            usuario.setPropietario(propietario);

            usuarioRepository.save(usuario);
            propietarioRepository.save(propietario);
        }

        else if ("VETERINARIO".equalsIgnoreCase(registroDTO.getRol())) {

            usuario.setEstado("INACTIVO"); // Requiere aprobación admin

            VeterinarioModel veterinario = new VeterinarioModel();
            veterinario.setNumLicencia(registroDTO.getNumLicencia());
            veterinario.setNombreVete(registroDTO.getNombreVete());
            veterinario.setApellidoVete(registroDTO.getApellidoVete());
            veterinario.setDireccionVete(registroDTO.getDireccionVete());
            veterinario.setTelefonoVete(registroDTO.getTelefonoVete());
            veterinario.setCorreoVete(registroDTO.getCorreoVete());
            veterinario.setUsuario(usuario);

            usuario.setVeterinario(veterinario);

            usuarioRepository.save(usuario);
            veterinarioRepository.save(veterinario);
        }

        else if ("FUNDACION".equalsIgnoreCase(registroDTO.getRol())) {

            usuario.setEstado("INACTIVO"); // equiere aprobación admin

            FundacionModel fundacion = new FundacionModel();
            fundacion.setNombre(registroDTO.getNombreFundacion());
            fundacion.setDescripcion(registroDTO.getDescripcionFundacion());
            fundacion.setDireccion(registroDTO.getDireccionFundacion());
            fundacion.setTelefono(registroDTO.getTelefonoFundacion());
            fundacion.setEmail(registroDTO.getEmailFundacion());
            fundacion.setUsuario(usuario);

            usuario.setFundacion(fundacion);

            usuarioRepository.save(usuario);
            fundacionRepository.save(fundacion);
        }

        else {
            throw new IllegalArgumentException("Rol no válido");
        }
    }


    //  ENVIAR SOLICITUD DE ACTIVACIÓN (usando sesión)
    public void enviarSolicitudPorUsername(String username) {

        UsuarioModel usuario =
                usuarioRepository.findByUsuario(username)
                        .orElseThrow(() ->
                                new RuntimeException("Usuario no encontrado"));

        if (usuario.getEstado().equals("INACTIVO")) {
            usuario.setEstado("PENDIENTE");
            usuarioRepository.save(usuario);
        }
    }

    //  Buscar usuario por username
    public UsuarioModel buscarPorUsername(String username) {
        return usuarioRepository.findByUsuario(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));
    }
}


