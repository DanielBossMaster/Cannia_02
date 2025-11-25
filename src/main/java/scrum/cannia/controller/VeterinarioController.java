package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import scrum.cannia.model.*;
import scrum.cannia.repository.*;

//MODIFICACION DE LOS SERVICE
import scrum.cannia.service.MascotaService;
import scrum.cannia.service.ProductoService;
import scrum.cannia.service.PropietarioService;
import scrum.cannia.service.VeterinarioService;


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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PropietarioService propietarioService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String index(HttpSession session, Model model) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }
        VeterinarioModel veterinario = usuario.getVeterinario();

        model.addAttribute("veterinario", veterinario);
        model.addAttribute("veterinarios", veterinarioRepository.findAll());
        model.addAttribute("propietarios", propietarioRepository.findByEstadoTrue());
        model.addAttribute("mascotas", mascotaRepository.findAll());
        model.addAttribute("propietario", new PropietarioModel());
        model.addAttribute("mascota", new MascotaModel());
        return "veterinario/index";


    }
    /// PARA REGISTRAR PROPIETARIO
    @PostMapping("/nuevo")
    public String nuevo(@Validated @ModelAttribute PropietarioModel propietarioModel, BindingResult br) {
        if (br.hasErrors()) {
            return "veterinario/index";
        } else {
            propietarioRepository.save(propietarioModel);
            return "redirect:/veterinario";
        }


    }
    /// PARA REGISTRARLE UNA MASCOTA A UN PROPIETARIO
    @PostMapping("/nuevom")
    public String guardarMascota(
            @ModelAttribute MascotaModel mascota,
            @RequestParam("propietarioId") Long propietarioId) {

        PropietarioModel propietario = propietarioService.obtenerPorId(propietarioId);
        mascota.setPropietario(propietario);
        mascotaService.guardar(mascota);

        return "redirect:/veterinario";
    }
    /// PARA BORRAR UN PROPIETARIO (LO CAMBIA DE ESTADO A INACTIVO)
    @PostMapping("/borrar/{id}")
    public String eliminarPropietario(@PathVariable Long id) {
        propietarioService.eliminarPropietario(id);
        return "redirect:/veterinario";
    }
    /// PARA DESPLEGAR EL FORMULARIO QUE ACTUALIZA EL PROPIETARIO
    @GetMapping("/actualizar/{id}")
    public String actualizarform (@PathVariable Long id, Model model){
        var propietarioEncontrado = propietarioRepository.findById(id).orElseThrow();
        model.addAttribute("propietario", propietarioEncontrado);
        return "veterinario/EditarPropietario";
    }
    /// PARA GUARDAR EL FORMULARIO DE EDITAR UN PROPIETARIO
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
    /// PARA IR A LA VETERINARIA (SI NO TIENE SE REGISTRA, SI YA TIENE SE VA ALA INICIO )
    @GetMapping("/miVeterinaria/{id}")
    public String gestionarVeterinaria(@PathVariable Long id, Model model) {
        if (veterinarioService.tieneVeterinaria(id)) {
            VeterinariaModel vet = veterinarioService.obtenerVeterinariaDeVeterinario(id);
            return "redirect:/veterinario/InicioVeterinaria/" + vet.getId();
        }
        // Si no tiene veterinaria, mostrar el formulario
        model.addAttribute("idVeterinario", id);
        model.addAttribute("veterinaria", new VeterinariaModel());
        return "veterinario/CrearVeterinaria";
    }

    /// MUESTRA PAGINA PRINCIPAL DE LA VETERINARIA
    @GetMapping("/InicioVeterinaria/{id}")
    public String inicioVeterinaria(@PathVariable Long id, Model model) {
        VeterinariaModel veterinaria = veterinarioService.obtenerVeterinariaDeVeterinario(id);
        model.addAttribute("veterinaria", veterinaria);
        return "veterinario/InicioVeterinaria";
    }

    /// GUARDA LA VETERINARIA AL REGISTRARLA
    @PostMapping("/CrearVeterinaria")
    public String crearVeterinaria(@RequestParam Long idVeterinario, @ModelAttribute VeterinariaModel vet) {
        VeterinariaModel nuevaVet = veterinarioService.crearVeterinaria(idVeterinario, vet);

        if (nuevaVet == null) {
            return "redirect:/veterinario/miVeterinaria/" + idVeterinario + "?error=YatieneVeterinaria";
        }

        return "redirect:/veterinario/InicioVeterinaria/" + nuevaVet.getId();
    }
    /// PASA EL OBJETO VACIO PARA PODER CREAR UNA VETERINARIA
    @GetMapping("/CrearVeterinaria/{id}")
    public String mostrarFormularioVeterinaria(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("veterinaria", new VeterinariaModel());
        return "veterinario/CrearVeterinaria";
    }

    @GetMapping("/GestionVentas")
    public String gestionVentas(Model model) {
        model.addAttribute("productos",productoService.listarTodos());
        return "veterinario/GestionVentas";
    }

    @GetMapping("/Tienda")
    public String tiendaPreview(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "veterinario/TiendaPreview";
    }

}