package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import scrum.cannia.Dto.RegistroDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private FundacionRepository fundacionRepository;

    @Autowired
    private VeterinariaRepository veterinariaRepository;


    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("registro", new RegistroDTO());
        model.addAttribute("veterinarias", veterinariaRepository.findAll());
        return "registro/registrar";
    }


    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public String registrarUsuario(@ModelAttribute("registro") RegistroDTO registroDTO, Model model) {

        try {

            // ===========================
            //   CREAR USUARIO BASE
            // ===========================
            UsuarioModel usuario = new UsuarioModel();
            usuario.setUsuario(registroDTO.getUsuario());
            usuario.setContrasena(registroDTO.getContrasena());
            usuario.setRol(registroDTO.getRol());

            // NO se guarda aquí para evitar usuarios huérfanos
            // usuarioRepository.save(usuario);


            // ===========================
            //   REGISTRO DE PROPIETARIO
            // ===========================
            if ("propietario".equalsIgnoreCase(registroDTO.getRol())) {

                if (registroDTO.getIdVeterinariaSeleccionada() == null) {
                    throw new IllegalArgumentException("Debe seleccionar una veterinaria.");
                }

                VeterinariaModel veterinariaSeleccionada = veterinariaRepository
                        .findById(registroDTO.getIdVeterinariaSeleccionada())
                        .orElseThrow(() -> new IllegalArgumentException("Veterinaria seleccionada no válida."));

                PropietarioModel propietario =
                        propietarioRepository.findByNumDoc(registroDTO.getNumDoc());

                if (propietario == null) {
                    propietario = new PropietarioModel();
                    propietario.setNumDoc(registroDTO.getNumDoc());
                    propietario.setNombrePro(registroDTO.getNombrePro());
                    propietario.setApellidoPro(registroDTO.getApellidoPro());
                    propietario.setDireccionPro(registroDTO.getDireccionPro());
                    propietario.setTelefonoPro(registroDTO.getTelefonoPro());
                    propietario.setCorreoPro(registroDTO.getCorreoPro());
                }

                propietario.setVeterinaria(veterinariaSeleccionada);
                propietario.setUsuario(usuario);

                usuario.setPropietario(propietario);

                usuarioRepository.save(usuario);
                propietarioRepository.save(propietario);
            }


            // ===========================
            //   REGISTRO DE VETERINARIO
            // ===========================
            else if ("veterinario".equalsIgnoreCase(registroDTO.getRol())) {

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


            // ===========================
            //   REGISTRO DE FUNDACIÓN
            // ===========================
            else if ("fundacion".equalsIgnoreCase(registroDTO.getRol())) {

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


            model.addAttribute("mensaje", "Registro exitoso");
            return "login/login";

        } catch (Exception e) {
            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            throw e;   // permite que @Transactional haga rollback
        }
    }
}
