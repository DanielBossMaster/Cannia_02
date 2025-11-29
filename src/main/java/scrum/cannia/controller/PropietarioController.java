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
import scrum.cannia.model.HistoriaClinicaModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.UsuarioModel;
import scrum.cannia.repository.HistoriaClinicaRepository;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.PropietarioRepository;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/propietario")
public class PropietarioController {

    private final PropietarioRepository propietarioRepository;
    private final MascotaRepository mascotaRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final ExcelExportService excelExportService;
    //private final ExcelLoaderService excelLoaderService;

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

    public PropietarioController(PropietarioRepository propietarioRepository,
                                 MascotaRepository mascotaRepository,
                                 HistoriaClinicaRepository historiaClinicaRepository,
                                 ExcelExportService excelExportService
                                 ) {
        this.propietarioRepository = propietarioRepository;
        this.mascotaRepository = mascotaRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.excelExportService = excelExportService;
        //this.excelLoaderService = excelLoaderService;
    }
    @GetMapping("/index")
    public String mostrarIndexPropietario(HttpSession session, Model model) {

        // Opcional: Recuperar el usuario desde la sesión
        UsuarioModel user = (UsuarioModel) session.getAttribute("usuario");

        model.addAttribute("usuario", user);

        return "Propietario/index";  // ⚠️ Respeta el nombre de tu carpeta/vista
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