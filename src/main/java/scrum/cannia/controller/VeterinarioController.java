package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Base64;
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
    @Autowired
    private ConsumidorBusquedaService consumidorBusquedaService;
    @Autowired
    private CategoriaService categoriaService;

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

        // --- Lógica de Seguridad y Carga de Entidades Principales ---
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        VeterinarioModel veterinario = usuario.getVeterinario();
        VeterinariaModel veterinaria = veterinario.getVeterinaria();

        // 1. Cargar Productos (Necesario para la tabla principal)
        model.addAttribute("productos", productoService.obtenerProductosActivos());

        // 2. Cargar Veterinaria (Necesario para el encabezado)
        model.addAttribute("veterinaria", veterinaria);

        // --- 🚨 ATRIBUTOS FALTANTES PARA EL MODAL DE CATEGORÍAS 🚨 ---

        // 3. Cargar TODAS las Categorías (Necesario para la tabla dentro del modal)
        // Se asume que tienes inyectado y funcionando 'categoriaService'.
        model.addAttribute("categorias", categoriaService.listarTodas());

        // 4. Inicializar un objeto CategoriaModel vacío
        // (Necesario para el th:object="${categoria}" del formulario POST en el modal)
        if (!model.containsAttribute("categoria")) {
            model.addAttribute("categoria", new CategoriaModel());
        }

        // -----------------------------------------------------------

        return "redirect:/veterinario/InicioVeterinaria";
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
    public String tienda(
            HttpSession session,
            Model model,
            // 1. Aceptar parámetros de URL: q (query) e idCategoria
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "idCategoria", required = false) Long idCategoria
    ) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        VeterinariaModel veterinaria = null;
        // ... (Tu lógica de obtención de usuario/veterinaria se mantiene igual) ...
        if (usuario.getPropietario() != null) {
            veterinaria = usuario.getPropietario().getVeterinaria();
            model.addAttribute("direccion", usuario.getPropietario().getDireccionPro());
        }

        if (veterinaria == null) {
            System.out.println("❌ El usuario NO pertenece a ninguna veterinaria");
            return "redirect:/";
        }
        model.addAttribute("veterinaria", veterinaria);

        // ------------------------------------------------------------------
        // LÓGICA DE CARGA DEL CATÁLOGO (COMPLETO O FILTRADO)
        // ------------------------------------------------------------------

        try {
            // Determinar si hay alguna búsqueda activa
            boolean esBusqueda = (q != null && !q.trim().isEmpty()) || (idCategoria != null);

            // Limpiar 'q' para el WS: si está vacío o solo con espacios, pásalo como null al WS
            String queryParaWS = (q == null || q.trim().isEmpty()) ? null : q.trim();

            // 2. Llamar al Web Service usando los parámetros (q, idCategoria)
            List<ProductoBusquedaDto> resultados =
                    productoService.obtenerProductosActivosFiltrados(queryParaWS, idCategoria); // <<<--- ¡AQUÍ ESTÁ EL CAMBIO!

            // 3. Pasar todos los datos necesarios a la vista:

            // Lista de productos (ya sea filtrada o completa)
            model.addAttribute("productos", resultados);

            // Lista de categorías para el dropdown de filtros
            model.addAttribute("categorias", categoriaService.listarTodas());

            // Contexto de la búsqueda (para mantener el estado y la lógica condicional en Thymeleaf)
            model.addAttribute("consulta", q); // Valor original para mostrar en la caja de texto
            model.addAttribute("categoriaSeleccionada", idCategoria); // ID seleccionado
            model.addAttribute("esBusqueda", esBusqueda); // Bandera para la vista

        } catch (Exception e) {
            System.err.println("Error al cargar o buscar productos: " + e.getMessage());
            model.addAttribute("errorBusqueda", "Error al procesar la solicitud de productos.");
            model.addAttribute("productos", List.of());
        }

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

        // Asumo que listarTodos() llama a un repositorio o servicio que devuelve ProductoModel
        List<ProductoModel> lista = productoService.obtenerProductosActivos();

        // 🔥 Convertimos foto (byte[]) → Base64 ANTES DE ENVIARLA A LA VISTA
        for (ProductoModel p : lista) {
            if (p.getFoto() != null) {

                // 1. Conversión simple a Base64
                String base64 = Base64.getEncoder().encodeToString(p.getFoto());

                // 2.  Agregar el prefijo Data URL
                // (Asumimos que el tipo de imagen es JPEG. Si usas PNG, cambia 'jpeg' a 'png')
                String dataUrl = "data:image/jpeg;base64," + base64;

                p.setFotoBase64(dataUrl);
            }
        }

        model.addAttribute("productos", lista);

        model.addAttribute("veterinaria", veterinaria);

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

        List<CategoriaModel> todasCategorias = categoriaService.listarTodas();

        model.addAttribute("producto", producto);
        model.addAttribute("todasCategorias", todasCategorias);

        return "veterinario/EditarProducto";
    }

    @PostMapping("/productos/guardar")
    public String actualizarProducto(
            @ModelAttribute ProductoModel producto,
            @RequestParam(required = false) MultipartFile imagen,
            HttpSession session
    ) throws IOException {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null || usuario.getVeterinario() == null) {
            return "redirect:/login";
        }

        productoService.actualizarC(producto, imagen);
        return "redirect:/veterinario/GestionVentas";
    }



    // 1. Méto de Carga Inicial (Ya lo tienes, asegura que 'categoria' esté siempre inicializado)
    @GetMapping("/admin/categorias")
    public String administrarCategorias(Model model) {
        // ... (Tu lógica de seguridad y carga de veterinaria si aplica) ...

        model.addAttribute("categorias", categoriaService.listarTodas());
        // Inicializa el objeto para que el formulario POST no falle al crear uno nuevo
        if (!model.containsAttribute("categoria")) {
            model.addAttribute("categoria", new CategoriaModel());
        }

        // Si vienes de un error de validación, Thymeleaf ya habrá puesto el objeto 'categoria'
        return "veterinario/GestionVentas"; // Retorna tu vista principal de gestión
    }

    // 2. Métod POST para Guardar/Actualizar (Se mantiene igual)
    @PostMapping("/admin/guardarCategoria")
    public String guardarCategoria(@ModelAttribute CategoriaModel categoria, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.guardar(categoria);
            // Puedes añadir un mensaje de éxito
        } catch (Exception e) {
            // Manejo de errores si aplica
            redirectAttributes.addFlashAttribute("categoria", categoria);
            // ... (añadir errores) ...
            return "redirect:/veterinario/GestionVentas";
        }
        return "redirect:/veterinario/GestionVentas";
    }
    // 3. NUEVO: Endpoint para cargar datos de la categoría vía AJAX (Recomendado: devuelve JSON)
// Nota: Usa @ResponseBody o @RestController para devolver JSON
    @GetMapping("/admin/categoria/{id}")
    @ResponseBody
    public CategoriaModel cargarDatosCategoria(@PathVariable Long id) {
        // Si el ID es 0 o null, podrías devolver un objeto vacío para "Crear Nuevo"
        if (id == 0) {
            return new CategoriaModel();
        }
        return categoriaService.obtenerPorId(id);
    }

    // 4. Eliminar (Se mantiene igual)
    @GetMapping("/admin/eliminarCategoria/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return "redirect:/veterinario/GestionVentas";
    }
}
