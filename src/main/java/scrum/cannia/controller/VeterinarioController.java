package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;

import scrum.cannia.service.*;

import java.io.IOException;
import java.util.List;

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
    @Autowired
    private FacturaService facturaService;
    @Autowired
    private FacturaRepository facturaRepository;

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
    public String gestionVentas(HttpSession session, Model model) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        VeterinarioModel veterinario = usuario.getVeterinario();
        VeterinariaModel veterinaria = veterinario.getVeterinaria();

        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("veterinaria", veterinaria);
        return "veterinario/GestionVentas";
    }

    // ============================================
    //              FORMULARIO PUBLICIDAD
    // ============================================
    @GetMapping("/FormularioPublicidad")
    public String publicidad(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        VeterinarioModel veterinario = usuario.getVeterinario();
        VeterinariaModel veterinaria = veterinario.getVeterinaria();

        model.addAttribute("publicidad", new PublicidadModel());
        model.addAttribute("veterinaria", veterinaria);

        return "veterinario/FormularioPublicidad";

    }
    // ============================================
    //                 TIENDA DE PROPIETARIO
    // ============================================
    @GetMapping("/Tienda")
    public String tienda(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = null;

        // SI EL USUARIO ES PROPIETARIO
        if (usuario.getPropietario() != null) {
            veterinaria = usuario.getPropietario().getVeterinaria();

            // ✅ NUEVO: obtener dirección de la CASA del propietario
            model.addAttribute("direccion",
                    usuario.getPropietario().getDireccionPro());
        }

        // SI EL USUARIO ES VETERINARIO
        if (usuario.getVeterinario() != null) {
            veterinaria = usuario.getVeterinario().getVeterinaria();
        }

        if (veterinaria == null) {
            System.out.println("❌ El usuario NO pertenece a ninguna veterinaria");
            return "redirect:/";
        }

        model.addAttribute("veterinaria", veterinaria);
        model.addAttribute("productos", productoService.listarTodos());

        return "veterinario/Tienda";
    }

    // ============================================
    //                 TIENDA DE VETERINARIO
    // ============================================

    @GetMapping("/TiendaPreview")
    public String tiendaPreview(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = null;


        // SI EL USUARIO ES VETERINARIO
        if (usuario.getVeterinario() != null) {
            veterinaria = usuario.getVeterinario().getVeterinaria();
        }



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
        model.addAttribute("producto", new ProductoModel());
        model.addAttribute("servicio", new ServicioModel());
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

    @GetMapping("/ventas")
    public String verVentas(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getVeterinario() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = usuario.getVeterinario().getVeterinaria();

        List<FacturaModel> ventas = facturaService.obtenerVentasVeterinaria(veterinaria.getId());

        model.addAttribute("ventas", ventas);
        model.addAttribute("veterinaria", veterinaria);

        return "veterinario/Ventas";


    }

    @PostMapping("/ventas/estado")
    @ResponseBody
    public void cambiarEstado(
            @RequestParam Long id,
            @RequestParam EstadoFactura estado
    ) {
        FacturaModel factura = facturaRepository.findById(id).orElseThrow();
        factura.setEstado(estado);
        facturaRepository.save(factura);
    }

    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Integer id,
            Model model,
            HttpSession session
    ) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null || usuario.getVeterinario() == null) {
            return "redirect:/login";
        }

        ProductoModel producto = productoService.buscarPorId(id);
        if (producto == null) {
            return "redirect:/veterinario/productos";
        }

        model.addAttribute("producto", producto);
        return "veterinario/EditarProducto";
    }

    @PostMapping("/veterinario/productos/editar")
    public String actualizarProducto(
            @ModelAttribute ProductoModel producto,
            @RequestParam(required = false) MultipartFile imagen,
            HttpSession session
    ) throws IOException {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null || usuario.getVeterinario() == null) {
            return "redirect:/login";
        }

        productoService.actualizar(producto, imagen);
        return "redirect:/veterinario/GestionVentas";
    }


}
