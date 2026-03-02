package scrum.cannia.controller;


import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.Dto.ProductoBusquedaDto;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;

import scrum.cannia.service.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/veterinario")
public class VeterinarioController {

    private final UsuarioRepository usuarioRepository;
    private final PropietarioService propietarioService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final ProductoService productoService;
    private final FacturaService facturaService;
    private final FacturaRepository facturaRepository;
    private final ConsumidorBusquedaService consumidorBusquedaService;
    private final CategoriaService categoriaService;
    private final ServicioService servicioService;
    private final VeterinarioRepository veterinarioRepository;
    private final PropietarioRepository propietarioRepository;
    private final MascotaRepository mascotaRepository;
    private final CodigoVinculacionService codigoVinculacionService;
    private final CitaService citaService;

    // ============================================
    //             DASHBOARD PRINCIPAL
    // ============================================

    @GetMapping("/index")
    public String Index(@RequestParam(required = false) Integer page, Authentication authentication, Model model) {

        String username = authentication.getName();

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(username)
                .orElseThrow();

        VeterinarioModel veterinario = usuario.getVeterinario();
        if (page == null) {
            page = 0;
        }

        //  PAGINACIÓN PROPIETARIOS
        Page<PropietarioModel> propietariosPage =
                propietarioService.listarPorVeterinario(veterinario, page, 8);

        model.addAttribute("veterinario", veterinario);
        model.addAttribute("propietarios", propietariosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", propietariosPage.getTotalPages());
        model.addAttribute("propietario", new PropietarioModel());
        model.addAttribute("mascota", new MascotaModel());
        model.addAttribute("citas", citaService.obtenerCitasPendientes());

        return "veterinario/index";
    }

    // ============================================
    //        REGISTRAR NUEVO PROPIETARIO
    // ============================================

    @PostMapping("/nuevo")
    public String nuevo(
            @Validated @ModelAttribute PropietarioModel propietarioModel,
            BindingResult br,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {

        if (br.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Por favor corrige los campos");
            return "redirect:/veterinario/index";
        }

        String username = authentication.getName();
        UsuarioModel usuario = usuarioRepository.findByUsuario(username).orElseThrow();

        VeterinarioModel veterinario = usuario.getVeterinario();

        // 🔑 ASOCIACIÓN CORRECTA
        propietarioModel.setVeterinario(veterinario);

        propietarioRepository.save(propietarioModel);

        return "redirect:/veterinario/index";
    }



    // ============================================
    // REGISTRAR MASCOTA A UN PROPIETARIO
    // ============================================
    @PostMapping("/nuevom")
    public String guardarMascota(@ModelAttribute MascotaModel mascota,
                                 @RequestParam("propietarioId") Long propietarioId) {

        PropietarioModel propietario = propietarioService.obtenerPorId(propietarioId);
        mascota.setPropietario(propietario);
        mascotaService.registrarMascota(mascota,propietario);

        return "redirect:/veterinario/index";
    }

    // ============================================
    //     CAMBIAR DE ESTADO (ELIMINAR PROPIETARIO)
    // ============================================
    @PostMapping("/borrar/{id}")
    public String eliminarPropietario(@PathVariable Long id) {
        propietarioService.eliminarPropietario(id);
        return "redirect:/veterinario/index";
    }

    // ============================================
    //    GUARDAR EDICIÓN DE PROPIETARIO
    // ============================================

    @PostMapping("/editar")
    public String actualizar(@RequestParam Long id,
                             @ModelAttribute PropietarioModel propietario) {
        propietarioService.actualizarPropietario(id,propietario);
        return "redirect:/veterinario/index";
    }
    // ============================================
    //        MÓDULO DE HISTORIA CLÍNICA
    // ============================================

    @GetMapping("/HistoriaClinica")
    public String mostrarPropietarioVH(
            Authentication authentication,
            Model model
    ) {
        // 1. Veterinario en sesión (Spring Security)
        String username = authentication.getName();
        UsuarioModel usuario = usuarioRepository.findByUsuario(username).orElseThrow();
        VeterinarioModel veterinario = usuario.getVeterinario();

        // 2. SOLO propietarios del veterinario y activos
        model.addAttribute(
                "propietarios",
                propietarioService.listarPorVeterinario(veterinario)
        );

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
    public String gestionVentas(
            @RequestParam(defaultValue = "0") int pageProductos,
            @RequestParam(defaultValue = "0") int pageServicios,
            @RequestParam(defaultValue = "productos") String vista,
            Authentication authentication,
            Model model
    ) {

        // 1. Usuario autenticado (Spring Security)
        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        VeterinarioModel veterinario = usuario.getVeterinario();

        // ⚠️ Puede que aún NO tenga veterinaria
        VeterinariaModel veterinaria = veterinario.getVeterinaria();

        // 2. Paginación de productos y servicios
        Page<ProductoModel> productosPage =
                productoService.listarActivosPaginado(
                        pageProductos,
                        9
                );

        Page<ServicioModel> serviciosPage =
                servicioService.listarActivosPorVeterinaria(
                        veterinaria.getId(),
                        pageServicios,
                        9
                );

        // 3. Datos para la vista
        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("currentPageProductos", pageProductos);
        model.addAttribute("totalPagesProductos", productosPage.getTotalPages());

        model.addAttribute("servicios", serviciosPage.getContent());
        model.addAttribute("currentPageServicios", pageServicios);
        model.addAttribute("totalPagesServicios", serviciosPage.getTotalPages());

        model.addAttribute("vistaActiva", vista);
        model.addAttribute("veterinaria", veterinaria);
        model.addAttribute("categorias", categoriaService.listarTodas());

        if (!model.containsAttribute("categoria")) {
            model.addAttribute("categoria", new CategoriaModel());
        }

        return "veterinario/GestionVentas";
    }

    // ============================================
    //              FORMULARIO PUBLICIDAD
    // ============================================

    @GetMapping("/FormularioPublicidad")
    public String publicidad(Authentication authentication, Model model) {

        // 1. Usuario autenticado (Spring Security)
        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        VeterinarioModel veterinario = usuario.getVeterinario();
        VeterinariaModel veterinaria = veterinario.getVeterinaria();

        // 2. Datos para la vista
        model.addAttribute("publicidad", new PublicidadModel());
        model.addAttribute("veterinaria", veterinaria);

        return "veterinario/FormularioPublicidad";
    }




    // ============================================
    //                 VENTAS
    // ============================================

    @GetMapping("/ventas")
    public String verVentas(Authentication authentication, Model model) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        // Seguridad: solo veterinarios
        if (usuario.getVeterinario() == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = usuario.getVeterinario().getVeterinaria();

        // Puede no tener veterinaria aún
        if (veterinaria == null) {
            return "redirect:/veterinario/index";
        }

        List<FacturaModel> ventas =
                facturaService.obtenerVentasVeterinaria(veterinaria.getId());

        model.addAttribute("ventas", ventas);
        model.addAttribute("veterinaria", veterinaria);

        return "veterinario/Ventas";
    }


    // ============================================
    //            VENTAS / ESTADO
    // ============================================

    @PostMapping("/ventas/estado")
    @ResponseBody
    public void cambiarEstado(
            @RequestParam Long id,
            @RequestParam EstadoFactura estado,
            Authentication authentication
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            throw new RuntimeException("No autorizado");
        }

        Integer veterinariaId = usuario.getVeterinario().getVeterinaria().getId();

        FacturaModel factura = facturaRepository
                .findByIdAndVeterinaria_Id(id, veterinariaId)
                .orElseThrow(() -> new RuntimeException("Factura no autorizada"));

        factura.setEstado(estado);
        facturaRepository.save(factura);
    }

    // ============================================
    //           PRODUCTOS / EDITAR
    // ============================================

    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable int id,
            Model model,
            Authentication authentication
    ) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            return "redirect:/login";
        }

        Integer veterinariaId = usuario.getVeterinario().getVeterinaria().getId();

        ProductoModel producto =
                productoService.obtenerProductoVeterinaria(id, veterinariaId);

        if (producto.getFoto() != null) {
            String base64 = Base64.getEncoder().encodeToString(producto.getFoto());
            producto.setFotoBase64(base64);
        }

        model.addAttribute("producto", producto);
        model.addAttribute("todasCategorias", categoriaService.listarTodas());

        return "veterinario/EditarProducto";
    }

    @PostMapping("/productos/guardar")
    public String actualizarProducto(
            @ModelAttribute ProductoModel producto,
            @RequestParam(required = false) MultipartFile imagen,
            Authentication authentication
    ) throws IOException {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        if (usuario.getVeterinario() == null ||
                usuario.getVeterinario().getVeterinaria() == null) {
            return "redirect:/login";
        }

        Integer veterinariaId = usuario.getVeterinario()
                .getVeterinaria()
                .getId();

        productoService.actualizarProductoVeterinaria(
                producto,
                imagen,
                veterinariaId
        );

        return "redirect:/veterinario/GestionVentas";
    }


    @GetMapping("/admin/categorias")
    public String administrarCategorias(Model model) {

        model.addAttribute("categorias", categoriaService.listarTodas());
        if (!model.containsAttribute("categoria")) {
            model.addAttribute("categoria", new CategoriaModel());
        }
        return "veterinario/GestionVentas";
    }


    @PostMapping("/admin/guardarCategoria")
    public String guardarCategoria(@ModelAttribute CategoriaModel categoria, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.guardar(categoria);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("categoria", categoria);
            return "redirect:/veterinario/GestionVentas";
        }
        return "redirect:/veterinario/GestionVentas";
    }

    @GetMapping("/admin/categoria/{id}")
    @ResponseBody
    public CategoriaModel cargarDatosCategoria(@PathVariable Long id) {

        if (id == 0) {
            return new CategoriaModel();
        }
        return categoriaService.obtenerPorId(id);
    }

    @GetMapping("/admin/eliminarCategoria/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return "redirect:/veterinario/GestionVentas";
    }

@PostMapping("/generarCodigo/{id}")
public String generarCodigo(
        @PathVariable Long id,
        Principal principal,
        RedirectAttributes redirect
) {

    try {
        VeterinarioModel veterinario =
                veterinarioService.buscarPorUsuario(principal.getName());

        PropietarioModel propietario =
                propietarioService.obtenerPorId(id);

        CodigoVinculacionModel codigo =
                codigoVinculacionService.generarCodigo(propietario, veterinario);

        redirect.addFlashAttribute("codigoGenerado", codigo.getCodigo());
        redirect.addFlashAttribute("propietarioCodigoId", propietario.getId());

    } catch (IllegalStateException e) {
        redirect.addFlashAttribute("errorCodigo", e.getMessage());
        redirect.addFlashAttribute("propietarioCodigoId", id);
    }

    return "redirect:/veterinario/index";
}
}
