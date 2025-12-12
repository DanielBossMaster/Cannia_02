package scrum.cannia.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.*;
import scrum.cannia.repository.*;
import scrum.cannia.service.*;
import scrum.cannia.strategy.DataLoaderStrategy;
import scrum.cannia.strategy.factory.DataLoaderFactory;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
@RequestMapping("/propietario")
public class PropietarioController {

    private final PropietarioRepository propietarioRepository;
    private final MascotaRepository mascotaRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final ExcelExportService excelExportService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PropietarioService propietarioService;
    @Autowired
    private HistoriaClinicaRepository historiaRepository;
    @Autowired
    private MascotaService mascotaService;
    @Autowired
    private VeterinarioService veterinarioService;
    @Autowired
    private ProductoService productoService;
    @Autowired

    public PropietarioController(PropietarioRepository propietarioRepository,
                                 MascotaRepository mascotaRepository,
                                 HistoriaClinicaRepository historiaClinicaRepository,
                                 ExcelExportService excelExportService
                                 ) {
        this.propietarioRepository = propietarioRepository;
        this.mascotaRepository = mascotaRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.excelExportService = excelExportService;
    }
    @GetMapping("/index")
    public String mostrarIndexPropietario(HttpSession session, Model model) {
        UsuarioModel user = (UsuarioModel) session.getAttribute("usuario");
        model.addAttribute("usuario", user);
        return "Propietario/index";
    }

    @GetMapping("/listar")
    public String listarPropietarios() {
        return "propietarios/lista";
    }

    @GetMapping("/detalles/{id}")
    public String verDetalles(@PathVariable Long id, Model model) {
        return "propietarios/detalles";
    }

    @GetMapping("/mascotas")
    public String verMascotas() {
        return "propietarios/mascotas";
    }


    //muestra las mascotas DISPONIBLES PARA ADOPCION
    @GetMapping("/mascotas/disponibles")
    public String mostrarMascotasDisponibles(Model model) {

        List<MascotaModel> disponibles =
                mascotaService.listarMascotasDisponibles();

        model.addAttribute("mascotas", disponibles);

        return "Propietario/MuestraMascotas";
    }


    // Boton de Tus Mascotas //
    @GetMapping("/{id}/mascotas")
    public String verMascotasDelPropietario(@PathVariable Long id, Model model) {

        PropietarioModel propietario = propietarioService.obtenerPorId(id);
        List<MascotaModel> mascotas = mascotaService.listarPorPropietario(id);

        model.addAttribute("propietario", propietario);
        model.addAttribute("mascotas", mascotas);

        return "Propietario/MascotasPropias";
    }

    // Muestra la mascota
    @GetMapping("/historia-clinica")
    public String seleccionarMascotaHistoria(HttpSession session, Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getPropietario() == null) {
            return "redirect:/login";
        }

        PropietarioModel propietario = usuario.getPropietario();

        List<MascotaModel> mascotas =
                mascotaService.listarPorPropietario(propietario.getId());

        model.addAttribute("mascotas", mascotas);

        return "Propietario/HistoriaClinicaMascotas";
    }

    // Historia Clinica para la mascota
    @GetMapping("/{id}/historia")
    public String verHistoriaClinica(@PathVariable Long id,
                                     HttpSession session,
                                     Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getPropietario() == null) {
            return "redirect:/login";
        }

        MascotaModel mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (mascota.getPropietario() == null ||
                usuario.getPropietario() == null ||
                !Objects.equals(
                        mascota.getPropietario().getId(),
                        usuario.getPropietario().getId())) {

            return "redirect:/errorRol";
        }

        List<HistoriaClinicaModel> historias =
                historiaRepository.findByMascotaIdOrderByFechaHoraDesc(id);

        model.addAttribute("mascota", mascota);
        model.addAttribute("historias", historias);

        return "Propietario/HistoriaClinicaDetalle";
    }

    //muestra detalle de la historia clinica
    @GetMapping("/mascota/{id}/historia")
    public String verHistoriaClinicaMascota(@PathVariable Long id,
                                            HttpSession session,
                                            Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

        if (usuario == null || usuario.getPropietario() == null) {
            return "redirect:/login";
        }

        MascotaModel mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        // 🔐 Seguridad: validar que la mascota sea del propietario en sesión
        if (mascota.getPropietario() == null ||
                !mascota.getPropietario().getId()
                        .equals(usuario.getPropietario().getId())) {

            return "redirect:/errorRol";
        }

        List<HistoriaClinicaModel> historias =
                historiaClinicaRepository
                        .findByMascotaIdOrderByFechaHoraDesc(id);

        model.addAttribute("mascota", mascota);
        model.addAttribute("historias", historias);

        // ⚠️ ESTA VISTA ES SOLO EL CONTENIDO DEL MODAL
        return "Propietario/HistoriaClinicaDetalle";
    }







    /**
     * Guarda la vacuna
     */

    @GetMapping
    public String listarPropietarios(Model model) {
        List<PropietarioModel> propietarios = propietarioRepository.findAll();

        // Cargar mascotas para cada propietario
        for (PropietarioModel propietario : propietarios) {
            propietario.setMascotas(mascotaRepository.findByPropietario(propietario));
        }

        // Agregar objeto vacío para el formulario
        model.addAttribute("historiaClinica", new HistoriaClinicaModel());
        model.addAttribute("propietarios", propietarios);

        return "veterinario/historiaclinica";
    }
    @PostMapping("/guardarVacuna")
    public String guardarVacuna(@RequestParam Long idPropietario,
                                @RequestParam Long idMascota,
                                @RequestParam String lote,
                                @RequestParam String fechaAplicacion,
                                @RequestParam(required = false) String fechaRefuerzo,
                                @RequestParam(required = false) String fechaVencimiento,
                                @RequestParam(required = false) String laboratorio){

        // Aquí va tu lógica para guardar la vacuna en la base de datos
        // Por ahora solo redirigimos
        System.out.println("Guardando vacuna para mascota: " + idMascota);

        return "redirect:/propietarios";
    }

    /**
     * Guarda la historia clínica y redirige a la misma página
     */
    @PostMapping("/guardarHistoria")
    public String guardarHistoriaClinica(@ModelAttribute HistoriaClinicaModel historiaClinica,
                                         @RequestParam Long mascotaId) {
        // Buscar la mascota
        Optional<MascotaModel> mascotaOpt = mascotaRepository.findById(mascotaId);
        if (mascotaOpt.isPresent()) {
            historiaClinica.setMascota(mascotaOpt.get());
            historiaClinicaRepository.save(historiaClinica);
        }

        return "redirect:/propietarios";
    }

    /**
     * Genera y descarga el archivo Excel
     */
    @GetMapping("/descargarHistoria/{historiaId}")
    public ResponseEntity<InputStreamResource> descargarHistoriaClinica(@PathVariable Long historiaId) {
        try {
            Optional<HistoriaClinicaModel> historiaOpt = historiaClinicaRepository.findById(historiaId);

            if (historiaOpt.isPresent()) {
                HistoriaClinicaModel historia = historiaOpt.get();
                MascotaModel mascota = historia.getMascota();
                PropietarioModel propietario = mascota.getPropietario();

                // Generar Excel
                ByteArrayInputStream excelStream = excelExportService.exportHistoriaClinicaToExcel(historia, mascota, propietario);

                // Configurar headers para la descarga
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition",
                        "attachment; filename=historia_clinica_" + mascota.getNomMascota() + ".xlsx");

                return ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(excelStream));
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}