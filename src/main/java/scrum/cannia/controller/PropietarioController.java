package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.Dto.CitaPropietarioDto;
import scrum.cannia.Dto.RecordatorioVacunaDto;
import scrum.cannia.model.*;
import scrum.cannia.repository.HistoriaClinicaRepository;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.CitaService;
import scrum.cannia.service.MascotaService;
import scrum.cannia.service.VacunaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/propietario")
public class PropietarioController {

    private final VacunaService vacunaService;
    private final UsuarioRepository usuarioRepository;
    private final MascotaService mascotaService;
    private final HistoriaClinicaRepository historiaRepository;
    private final CitaService citaService;

    // ============================================
    //           DASHBOARD PROPIETARIO
    // ============================================

    @GetMapping("/index")
    public String indexPropietario(
            Authentication authentication,
            Model model) {

        PropietarioModel propietario = obtenerPropietario(authentication);

        List<MascotaModel> mascotas =
                mascotaService.listarConHistoriaYVacunas(propietario);

        List<RecordatorioVacunaDto> recordatoriosVacunas =
                vacunaService.obtenerRecordatoriosVacunas(propietario);

        List<RecordatorioVacunaDto> recordatorios =
                vacunaService.obtenerRecordatoriosVacunas(propietario);

        List<CitaPropietarioDto> citas =
                citaService.obtenerCitasPropietario(propietario);


        model.addAttribute("recordatoriosVacunas", recordatoriosVacunas);
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("mascota", new MascotaModel());
        model.addAttribute("propietario", propietario);
        model.addAttribute("citas", citas);


        return "propietario/index";
    }

    // ============================================
    //         REGISTRAR NUEVA MASCOTA
    // ============================================

    @PostMapping("/mascota/guardar")
    public String guardarMascota(
            @ModelAttribute MascotaModel mascota,
            @RequestParam("archivoFoto") MultipartFile archivoFoto,
            Authentication authentication,
            RedirectAttributes redirectAttributes

    ) throws IOException {

        PropietarioModel propietario = obtenerPropietario(authentication);

        // Si el usuario sube imagen
        if (!archivoFoto.isEmpty()) {

            // generar nombre único
            String nombreArchivo =
                    System.currentTimeMillis() + "_" +
                            archivoFoto.getOriginalFilename();

            // ruta donde se guardará
            Path rutaCarpeta =
                    Paths.get("src/main/resources/static/uploads/mascotas");

            // crear carpeta si no existe
            Files.createDirectories(rutaCarpeta);

            // guardar archivo
            Files.write(
                    rutaCarpeta.resolve(nombreArchivo),
                    archivoFoto.getBytes()
            );

            // guardar nombre en la entidad
            mascota.setFoto(nombreArchivo);
        }

        mascotaService.registrarMascota(mascota, propietario);
        redirectAttributes.addFlashAttribute("mensajeExito", "Mascota registrada correctamente");

        return "redirect:/propietario/index";
    }

    // ============================================
    //               EDITAR MASCOTAS
    // ============================================

    @PostMapping("/mascota/editar")
    public String actualizarMascota(
            @ModelAttribute MascotaModel mascota,
            @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto,
            RedirectAttributes redirectAttributes
    ) throws IOException {

        // si el usuario sube una nueva foto
        if (archivoFoto != null && !archivoFoto.isEmpty()) {

            String nombreArchivo =
                    System.currentTimeMillis() + "_" +
                            archivoFoto.getOriginalFilename();

            Path rutaCarpeta =
                    Paths.get("src/main/resources/static/uploads/mascotas");

            Files.createDirectories(rutaCarpeta);

            Files.write(
                    rutaCarpeta.resolve(nombreArchivo),
                    archivoFoto.getBytes()
            );

            mascota.setFoto(nombreArchivo);
        }

        mascotaService.actualizarMascota(mascota);

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Mascota actualizada correctamente "
        );

        return "redirect:/propietario/index";
    }

    @PostMapping("/mascota/eliminar")
    public String eliminarMascota(
            @RequestParam Long id,
            RedirectAttributes redirectAttributes
    ){

        mascotaService.eliminarMascotaLogico(id);

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Mascota eliminada correctamente "
        );

        return "redirect:/propietario/index";
    }

    // ============================================
    //     HISTORIA CLÍNICA (DETALLE MASCOTA)
    // ============================================

    @GetMapping("/mascota/{id}/historia")
    public String verHistoriaClinica(
            @PathVariable Long id,
            Authentication authentication,
            Model model
    ) {

        PropietarioModel propietario = obtenerPropietario(authentication);

        MascotaModel mascota =
                mascotaService.obtenerMascotaPropietario(id, propietario);

        model.addAttribute("mascota", mascota);
        model.addAttribute("historias", historiaRepository.findByMascotaIdOrderByFechaHoraDesc(id));

        return "propietario/index";
    }

    // ======================================================
    //     MetoDO PRIVADO AUXILIAR ( CLAVE DE SEGURIDAD)
    // ======================================================

    private PropietarioModel obtenerPropietario(Authentication authentication) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("Usuario no encontrado"));

        if (usuario.getPropietario() == null) {
            throw new IllegalStateException("El usuario no es propietario");
        }

        return usuario.getPropietario();
    }

}