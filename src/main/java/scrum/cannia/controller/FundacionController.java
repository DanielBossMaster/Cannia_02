package scrum.cannia.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.Dto.ResultadoCargaMascotasDTO;
import scrum.cannia.model.*;
import scrum.cannia.repository.UsuarioRepository;
import scrum.cannia.service.*;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.strategy.DataLoaderStrategy;
import scrum.cannia.strategy.factory.DataLoaderFactory;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Controller
@RequestMapping("/fundacion")
public class FundacionController {

    private final Cloudinary cloudinary;
    private final MascotaService mascotaService;
    private final MascotaRepository mascotaRepository;
    private final MascotaServiceCreator mascotaServiceCreator;
    private final UsuarioRepository usuarioRepository;
    private final FundacionService fundacionService;
    private final UsuarioService usuarioService;
    private final SolicitudAdopcionService solicitudService;

    @GetMapping("/index")
    public String dashboard(Authentication authentication, Model model) {

        UsuarioModel usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow();

        FundacionModel fundacion = usuario.getFundacion();

        if (fundacion == null) {
            return "redirect:/login";
        }

        List<MascotaModel> mascotas = mascotaService.obtenerMascotasFundacion(fundacion);

        List<SolicitudAdopcionModel> solicitudes = solicitudService.obtenerSolicitudesFundacion(fundacion);


        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("mascota", new MascotaModel());
        model.addAttribute("fundacion", fundacion);
        model.addAttribute("fundacionId", fundacion.getId());
        model.addAttribute("mascotas", mascotas);


        return "fundacion/index";
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

        fundacionService.actualizarCampo(

                username,
                datos.get("campo"),
                datos.get("valor")

        );

        return "ok";
    }
    @GetMapping("/CargarMascotas")
    public String vistaCargarMascotas() {
        return "fundacion/CargarMascotas";
    }

    @PostMapping("/upload")
    public String cargarMascotasFundacion(
            @RequestParam("archivo") MultipartFile file,
            HttpSession session,
            Model model) {

        Long fundacionId = (Long) session.getAttribute("fundacionId");

        if (fundacionId == null) {
            model.addAttribute("error", "No hay fundación en sesión.");
            return "redirect:/login";
        }

        try {
            // 1️⃣ Obtener estrategia según extensión
            String filename = file.getOriginalFilename();
            DataLoaderStrategy strategy = DataLoaderFactory.getStrategy(filename);

            // 2️⃣ Leer archivo (Strategy)
            List<MascotaCargaDTO> mascotasDTO = strategy.loadData(file);

            // 3️⃣ Crear mascotas usando TEMPLATE METHOD
            ResultadoCargaMascotasDTO resultado =
                    mascotaServiceCreator.crearDesdeFundacion(
                            mascotasDTO,
                            fundacionId
                    );

            // 4️⃣ Enviar resultados a la vista
            model.addAttribute("total", resultado.getGuardadas().size());
            model.addAttribute("errores", resultado.getErrores());

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "fundacion/CargarMascotas";
    }


    @PostMapping("/mascota/registrar")
    public String registrarMascotaFundacion(
            @ModelAttribute MascotaModel mascota,
            @RequestParam("fotoMascota") MultipartFile fotoMascota,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            if(!fotoMascota.isEmpty()){

                Map resultado = cloudinary.uploader().upload(

                        fotoMascota.getBytes(),

                        ObjectUtils.asMap(
                                "folder", "mascotas"
                        )
                );

                String urlImagen =
                        resultado.get("secure_url").toString();

                mascota.setFoto(urlImagen);
            }

            String username = authentication.getName();

            UsuarioModel usuario = usuarioService.buscarPorUsername(username);

            FundacionModel fundacion = fundacionService.buscarPorUsuario(usuario);

            mascotaService.registrarMascotaFundacion(mascota, fundacion);

            redirectAttributes.addFlashAttribute("success",
                    "Mascota registrada correctamente");

        } catch (Exception e) {

            e.printStackTrace();

            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar mascota");

        }

        return "redirect:/fundacion/index";
    }

    @PostMapping("/mascota/editar")
    public String actualizarMascota(
            @ModelAttribute MascotaModel mascota,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "fotoMascota", required = false) MultipartFile fotoMascota) {

        try {
            if(!fotoMascota.isEmpty()){

                Map resultado = cloudinary.uploader().upload(

                        fotoMascota.getBytes(),

                        ObjectUtils.asMap(
                                "folder", "mascotas"
                        )
                );

                String urlImagen =
                        resultado.get("secure_url").toString();

                mascota.setFoto(urlImagen);
            }

            mascotaService.actualizarMascota(mascota);

            redirectAttributes.addFlashAttribute("success",
                    "Mascota actualizada correctamente");

        } catch (Exception e) {

            e.printStackTrace();

            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar mascota");
        }

        return "redirect:/fundacion/index";
    }

    @PostMapping("/solicitud/aceptar")
    public String aceptarSolicitud(@RequestParam Long solicitudId) {

        solicitudService.aceptarSolicitud(solicitudId);

        return "redirect:/fundacion/index";
    }

    @PostMapping("/solicitud/rechazar")
    public String rechazarSolicitud(@RequestParam Long solicitudId){

        solicitudService.rechazarSolicitud(solicitudId);

        return "redirect:/fundacion/index";
    }
}