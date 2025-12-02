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

import scrum.cannia.service.MascotaService;
import scrum.cannia.service.ProductoService;
import scrum.cannia.service.PropietarioService;
import scrum.cannia.service.VeterinarioService;

@Controller
@RequestMapping("/veterinario")
public class VeterinarioController {

    private final VeterinarioRepository veterinarioRepository;
    private final PropietarioRepository propietarioRepository;
    private final MascotaRepository mascotaRepository;

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

    public VeterinarioController(
            VeterinarioRepository veterinarioRepository,
            PropietarioRepository propietarioRepository,
            MascotaRepository mascotaRepository) {

        this.propietarioRepository = propietarioRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.mascotaRepository = mascotaRepository;
    }

    // ============================================
    //               DASHBOARD PRINCIPAL
    // ============================================
    @GetMapping
    public String Index(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        VeterinarioModel veterinario = usuario.getVeterinario();

        model.addAttribute("veterinario", veterinario);
        model.addAttribute("veterinarios", veterinarioRepository.findAll());
        model.addAttribute("propietarios", propietarioRepository.findByEstadoTrue());
        model.addAttribute("mascotas", mascotaRepository.findAll());
        model.addAttribute("propietario", new PropietarioModel());
        model.addAttribute("mascota", new MascotaModel());

        return "veterinario/Index";
    }

    // ============================================
//        REGISTRAR NUEVO PROPIETARIO
// ============================================
    @PostMapping("/nuevo")
    public String nuevo(
            @Validated @ModelAttribute("propietarioModel") PropietarioModel propietarioModel,
            BindingResult br,
            HttpSession session,
            Model model) {

        // Validación del formulario
        if (br.hasErrors()) {
            model.addAttribute("mensajeError", "Por favor corrige los campos marcados.");
            return "veterinario/Index";
        }

        // Obtener el usuario en sesión
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";  // Seguridad
        }

        // Obtener veterinario y veterinaria asociada
        VeterinarioModel veterinario = usuario.getVeterinario();
        VeterinariaModel veterinaria = veterinario.getVeterinaria();

        // Asignar automáticamente la veterinaria al propietario que se está registrando
        propietarioModel.setVeterinaria(veterinaria);

        // Guardar propietario
        propietarioRepository.save(propietarioModel);

        return "redirect:/veterinario";  // Volver al listado
    }



    // ============================================
    // REGISTRAR MASCOTA A UN PROPIETARIO
    // ============================================
    @PostMapping("/nuevom")
    public String guardarMascota(@ModelAttribute MascotaModel mascota,
                                 @RequestParam("propietarioId") Long propietarioId) {

        PropietarioModel propietario = propietarioService.obtenerPorId(propietarioId);
        mascota.setPropietario(propietario);
        mascotaService.guardar(mascota);

        return "redirect:/veterinario";
    }

    // ============================================
    //     CAMBIAR DE ESTADO (ELIMINAR PROPIETARIO)
    // ============================================
    @PostMapping("/borrar/{id}")
    public String eliminarPropietario(@PathVariable Long id) {
        propietarioService.eliminarPropietario(id);
        return "redirect:/veterinario";
    }

    // ============================================
    //      FORMULARIO DE ACTUALIZACIÓN
    // ============================================
    @GetMapping("/actualizar/{id}")
    public String actualizarform(@PathVariable Long id, Model model) {

        var propietarioEncontrado = propietarioRepository.findById(id).orElseThrow();
        model.addAttribute("propietario", propietarioEncontrado);

        return "veterinario/EditarPropietario";
    }

    // ============================================
    //    GUARDAR EDICIÓN DE PROPIETARIO
    // ============================================
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute PropietarioModel cambios) {

        PropietarioModel existente = propietarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Propietario no encontrado"));

        // Actualizar solo campos llenos
        if (cambios.getNombrePro() != null && !cambios.getNombrePro().isBlank())
            existente.setNombrePro(cambios.getNombrePro());

        if (cambios.getApellidoPro() != null && !cambios.getApellidoPro().isBlank())
            existente.setApellidoPro(cambios.getApellidoPro());

        if (cambios.getDireccionPro() != null && !cambios.getDireccionPro().isBlank())
            existente.setDireccionPro(cambios.getDireccionPro());

        if (cambios.getTelefonoPro() != null && !cambios.getTelefonoPro().isBlank())
            existente.setTelefonoPro(cambios.getTelefonoPro());

        if (cambios.getCorreoPro() != null && !cambios.getCorreoPro().isBlank())
            existente.setCorreoPro(cambios.getCorreoPro());

        propietarioRepository.save(existente);
        return "redirect:/veterinario";
    }

    // ============================================
    //        MÓDULO DE HISTORIA CLÍNICA
    // ============================================
    @GetMapping("/HistoriaClinica")
    public String mostrarPropietarioVH(Model model) {
        model.addAttribute("propietarios", propietarioRepository.findByEstadoTrue());
        return "veterinario/HistoriaClinica";
    }

    // ============================================
    //   GESTIONAR VETERINARIA DEL VETERINARIO
    // ============================================
    @GetMapping("/miVeterinaria/{id}")
    public String gestionarVeterinaria(@PathVariable Long id, Model model) {

        if (veterinarioService.tieneVeterinaria(id)) {

            VeterinariaModel vet = veterinarioService.obtenerVeterinariaDeVeterinario(id);
            return "redirect:/veterinario/InicioVeterinaria/" + vet.getId();
        }

        model.addAttribute("idVeterinario", id);
        model.addAttribute("veterinaria", new VeterinariaModel());

        return "veterinario/CrearVeterinaria";
    }

    @GetMapping("/InicioVeterinaria/{id}")
    public String inicioVeterinaria(@PathVariable Long id, Model model) {

        VeterinariaModel veterinaria = veterinarioService.obtenerVeterinariaDeVeterinario(id);
        model.addAttribute("veterinaria", veterinaria);

        return "veterinario/InicioVeterinaria";
    }

    @PostMapping("/CrearVeterinaria")
    public String crearVeterinaria(@RequestParam Long idVeterinario,
                                   @ModelAttribute VeterinariaModel vet) {

        VeterinariaModel nuevaVet = veterinarioService.crearVeterinaria(idVeterinario, vet);

        if (nuevaVet == null) {
            return "redirect:/veterinario/miVeterinaria/" + idVeterinario + "?error=YatieneVeterinaria";
        }

        return "redirect:/veterinario/InicioVeterinaria/" + nuevaVet.getId();
    }

    @GetMapping("/CrearVeterinaria/{id}")
    public String mostrarFormularioVeterinaria(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("veterinaria", new VeterinariaModel());
        return "veterinario/CrearVeterinaria";
    }

    // ============================================
    //              GESTIÓN DE VENTAS
    // ============================================
    @GetMapping("/GestionVentas")
    public String gestionVentas(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "veterinario/GestionVentas";
    }

    // ============================================
    //              FORMULARIO PUBLICIDAD
    // ============================================
    @GetMapping("/FormularioPublicidad")
    public String publicidad(Model model) {

        model.addAttribute("publicidad", new PublicidadModel());

        return "veterinario/FormularioPublicidad";

    }
    // ============================================
    //                 TIENDA PREVIEW
    // ============================================
    @GetMapping("/Tienda")
    public String tiendaPreview(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        VeterinarioModel veterinario = usuario.getVeterinario();
        VeterinariaModel veterinaria = veterinario.getVeterinaria();

        model.addAttribute("veterinaria", veterinaria);
        model.addAttribute("productos", productoService.listarTodos());

        return "veterinario/TiendaPreview";
    }
    // ============================================
    //          EDICION DE INVENTARIO
    // ============================================
    @GetMapping("/inventario")
    public String mostrarInventarioVentas(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "veterinario/inventario";
    }
    @GetMapping("/productos")
    public String listarProductosVeterinario(Model model) {
        // Usar el método correcto que ahora existe
        model.addAttribute("productos", productoService.listarTodos());
        return "veterinario/productos";
    }

    @GetMapping("/productos/activos")
    public String listarProductosActivos(Model model) {
        // O usar este método si quieres solo los activos
        model.addAttribute("productos", productoService.obtenerProductosActivos());
        return "veterinario/productos";
    }
}
