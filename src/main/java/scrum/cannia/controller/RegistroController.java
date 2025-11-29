package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import scrum.cannia.dto.RegistroDTO;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.VeterinarioModel;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.repository.PropietarioRepository;
import scrum.cannia.repository.VeterinarioRepository;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("registro", new RegistroDTO());
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

                // Buscar si ya existe un propietario con este documento
                PropietarioModel propietarioExistente =
                        propietarioRepository.findByNumDoc(registroDTO.getNumDoc());

                PropietarioModel propietario;

                if (propietarioExistente != null) {
                    // ✔ Caso 1: el propietario YA existe → lo usamos
                    propietario = propietarioExistente;

                } else {
                    // ✔ Caso 2: no existe → creamos uno nuevo
                    propietario = new PropietarioModel();
                    propietario.setNumDoc(registroDTO.getNumDoc());
                    propietario.setNombrePro(registroDTO.getNombrePro());
                    propietario.setApellidoPro(registroDTO.getApellidoPro());
                    propietario.setDireccionPro(registroDTO.getDireccionPro());
                    propietario.setTelefonoPro(registroDTO.getTelefonoPro());
                    propietario.setCorreoPro(registroDTO.getCorreoPro());

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

