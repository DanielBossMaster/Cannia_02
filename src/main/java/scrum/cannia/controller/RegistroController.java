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

import java.lang.IllegalArgumentException;


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
    @Transactional
    public String registrarUsuario(@ModelAttribute("registro") RegistroDTO registroDTO, Model model) {
        try {
            // Crear usuario
            UsuarioModel usuario = new UsuarioModel();
            usuario.setUsuario(registroDTO.getUsuario());
            usuario.setContrasena(registroDTO.getContrasena());
            usuario.setRol(registroDTO.getRol());
            usuarioRepository.save(usuario);

            // ===========================
            //   REGISTRO DE PROPIETARIO
            // ===========================
            if ("propietario".equalsIgnoreCase(registroDTO.getRol())) {

                // 1. Validar la selección de Veterinaria
                if (registroDTO.getIdVeterinariaSeleccionada() == null) {
                    throw new IllegalArgumentException("Debe seleccionar una veterinaria.");
                }

                // 2. Buscar la Veterinaria seleccionada
                VeterinariaModel veterinariaSeleccionada = veterinariaRepository
                        .findById(registroDTO.getIdVeterinariaSeleccionada())
                        .orElseThrow(() -> new IllegalArgumentException("Veterinaria seleccionada no válida."));

                // ... (Lógica de búsqueda de PropietarioExistente, igual que antes) ...
                PropietarioModel propietarioExistente =
                        propietarioRepository.findByNumDoc(registroDTO.getNumDoc());

                PropietarioModel propietario;

                if (propietarioExistente != null) {
                    propietario = propietarioExistente;

                    // Asegurar que si ya existe un propietario, se asocie la veterinaria seleccionada
                    // (O manejar la lógica si el propietario existente ya tiene una veterinaria diferente)
                    if (propietario.getVeterinaria() == null) {
                        propietario.setVeterinaria(veterinariaSeleccionada);
                    }

                } else {
                    propietario = new PropietarioModel();
                    propietario.setNumDoc(registroDTO.getNumDoc());
                    propietario.setNombrePro(registroDTO.getNombrePro());
                    propietario.setApellidoPro(registroDTO.getApellidoPro());
                    propietario.setDireccionPro(registroDTO.getDireccionPro());
                    propietario.setTelefonoPro(registroDTO.getTelefonoPro());
                    propietario.setCorreoPro(registroDTO.getCorreoPro());

                    // **********************************
                    // ** ASIGNACIÓN DE LA VETERINARIA SELECCIONADA **
                    // **********************************
                    propietario.setVeterinaria(veterinariaSeleccionada);

                    propietarioRepository.save(propietario);
                }

                // Asociar usuario ↔ propietario
                propietario.setUsuario(usuario);
                usuario.setPropietario(propietario);

                propietarioRepository.save(propietario);
                usuarioRepository.save(usuario);
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

                veterinarioRepository.save(veterinario);

                usuario.setVeterinario(veterinario);
                usuarioRepository.save(usuario);

            } else if ("fundacion".equalsIgnoreCase(registroDTO.getRol())) {
                FundacionModel fundacion = new FundacionModel();
                fundacion.setNombre(registroDTO.getNombreFundacion());
                fundacion.setDescripcion(registroDTO.getDescripcionFundacion());
                fundacion.setDireccion(registroDTO.getDireccionFundacion());
                fundacion.setTelefono(registroDTO.getTelefonoFundacion());
                fundacion.setEmail(registroDTO.getEmailFundacion());
                fundacion.setUsuario(usuario);

                fundacionRepository.save(fundacion);

                usuario.setFundacion(fundacion);
                usuarioRepository.save(usuario);
            }

            model.addAttribute("mensaje", "Registro exitoso");
            return "login/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            return "registro/registrar";
        }
    }

}

