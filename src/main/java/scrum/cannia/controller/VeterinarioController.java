package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;

import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.PropietarioRepository;
import scrum.cannia.repository.VeterinarioRepository;

import scrum.cannia.service.VeterinarioService;
import scrum.cannia.service.MascotaService;
import scrum.cannia.service.PropietarioService;



@Controller
@RequestMapping("/veterinario")
public class  VeterinarioController {

    private final VeterinarioRepository veterinarioRepository;
    private final PropietarioRepository propietarioRepository;
    private final MascotaRepository mascotaRepository;


    public VeterinarioController(
            VeterinarioRepository veterinarioRepository,
            PropietarioRepository propietarioRepository,
            MascotaRepository mascotaRepository) {

        this.propietarioRepository = propietarioRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.mascotaRepository = mascotaRepository;


    }

    @Autowired
    private PropietarioService propietarioService;

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login"; // Redirige si no hay sesión
        }
        model.addAttribute("veterinarios", veterinarioRepository.findAll());
        model.addAttribute("propietarios", propietarioRepository.findByEstadoTrue());
        model.addAttribute("mascotas", mascotaRepository.findAll());
        model.addAttribute("propietario", new PropietarioModel());
        model.addAttribute("mascota", new MascotaModel());
        return "veterinario/index";


    }

    @PostMapping("/nuevo")
    public String nuevo(@Validated @ModelAttribute PropietarioModel propietarioModel, BindingResult br) {
        if (br.hasErrors()) {
            return "veterinario/index";
        } else {
            propietarioRepository.save(propietarioModel);
            return "redirect:/veterinario";
        }


    }


    @PostMapping("/nuevom")
    public String guardarMascota(
            @ModelAttribute MascotaModel mascota,
            @RequestParam("propietarioId") Long propietarioId) {

        PropietarioModel propietario = propietarioService.obtenerPorId(propietarioId);
        mascota.setPropietario(propietario);
        mascotaService.guardar(mascota);

        return "redirect:/veterinario";
    }



    @PostMapping("/borrarp/{id}")
    public String eliminarPropietario(@PathVariable Long id) {
        propietarioService.eliminarPropietario(id);
        return "redirect:/veterinario";
    }

    @GetMapping("/actualizar/{id}")
    public String actualizarform (@PathVariable Long id, Model model){
        var propitarioEncontrado = propietarioRepository.findById(id).orElseThrow();
        model.addAttribute("propietario", propitarioEncontrado);
        return "veterinario/EditarPropietario";
    }

    @PostMapping("/editar/{id}")
    public String actualizar (@PathVariable Long id, @ModelAttribute PropietarioModel cambios){
        PropietarioModel existente = propietarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Propietario no encontrado"));
        // Solo actualiza si el usuario ingresó algo
            if (cambios.getNombrePro() != null && !cambios.getNombrePro().isBlank()) {
                existente.setNombrePro(cambios.getNombrePro());
            }
            if (cambios.getApellidoPro() != null && !cambios.getApellidoPro().isBlank()) {
                existente.setApellidoPro(cambios.getApellidoPro());
            }
            if (cambios.getDireccionPro() != null && !cambios.getDireccionPro().isBlank()) {
                existente.setDireccionPro(cambios.getDireccionPro());
            }
            if (cambios.getTelefonoPro() != null && !cambios.getTelefonoPro().isBlank()) {
                existente.setTelefonoPro(cambios.getTelefonoPro());
            }
            if (cambios.getCorreoPro() != null && !cambios.getCorreoPro().isBlank()) {
                existente.setCorreoPro(cambios.getCorreoPro());
            }
            propietarioRepository.save(existente);
            return "redirect:/veterinario";
    }
/// MUESTRA VISTA DE MASCOTAS
    @GetMapping("/HistoriaClinica")
        public String mostrarPropietarioVH (Model model){
            model.addAttribute("propietarios", propietarioRepository.findByEstadoTrue());
            return "veterinario/HistoriaClinica";
        }

    }

