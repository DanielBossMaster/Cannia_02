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
import scrum.cannia.service.PropietarioService;
import scrum.cannia.service.VacunaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@AllArgsConstructor
@Controller
@RequestMapping("/propietario")
public class PropietarioController {

    private final VacunaService vacunaService;
    private final UsuarioRepository usuarioRepository;
    private final MascotaService mascotaService;
    private final HistoriaClinicaRepository historiaRepository;
    private final CitaService citaService;
    private final  Cloudinary cloudinary;
    private final PropietarioService propietarioService;

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
    //        EDITAR PERFIL Propietario
    // ============================================

    @PostMapping("/actualizar-campo")
    @ResponseBody
    public String actualizarCampo(
            @RequestBody Map<String,String> datos,
            Authentication authentication
    ) {

       String username = authentication.getName() ;

        propietarioService.actualizarCampo(

                username,
                datos.get("campo"),
                datos.get("valor")

        );

        return "ok";
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
            Map resultado =
                    cloudinary.uploader().upload(
                            archivoFoto.getBytes(),
                            ObjectUtils.asMap(
                                    "folder", "mascotas"
                            )
                    );
            String urlImagen =
                    resultado.get("secure_url").toString();
            mascota.setFoto(urlImagen);
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

        if(archivoFoto != null && !archivoFoto.isEmpty()){

            Map resultado =
                    cloudinary.uploader().upload(

                            archivoFoto.getBytes(),
                            ObjectUtils.asMap(
                                    "folder", "mascotas"
                            )
                    );

            mascota.setFoto(
                    resultado.get("secure_url").toString()
            );
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