package scrum.cannia.controller;


import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.repository.PropietarioRepository;
import scrum.cannia.service.EmailService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/publicidad")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @PostMapping("/enviar")
    public String enviarPublicidad(@RequestParam String titulo,
                                   @RequestParam String mensaje,
                                   @RequestParam(required = false) MultipartFile imagen,
                                   @RequestParam(required = false) String correosManuales
    ) throws IOException {

        // 1. Correos desde la BD
        List<String> correos = propietarioRepository.obtenerCorreosDePropietarios();

        // 2. Agregar correos manuales
        if (correosManuales != null && !correosManuales.trim().isEmpty()) {

            // dividir por comas o saltos de línea
            String[] extras = correosManuales.split("[,\\n]");

            for (String c : extras) {
                String correo = c.trim();
                if (!correo.isEmpty() && correo.contains("@")) {
                    correos.add(correo); // agregar a la lista final
                }
            }
        }

        // 3. Guardar imagen si existe
        String urlImagen = null;
        if (imagen != null && !imagen.isEmpty()) {
            urlImagen = guardarImagen(imagen);
        }

        // 4. Construir HTML
        String mensajeHtml = construirCorreoHtml(titulo, mensaje, urlImagen);

        // 5. Enviar correo masivo
        emailService.enviarMasivo(correos, titulo, mensajeHtml);

        return "redirect:/veterinario/GestionVentas";
    }


    private String guardarImagen(MultipartFile file) throws IOException {

        String nombre = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String ruta = "src/main/resources/static/publicidad/" + nombre;

        Path path = Paths.get(ruta);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return "/publicidad/" + nombre; // URL pública
    }

    private String construirCorreoHtml(String titulo, String mensaje, String imagenUrl) {
        StringBuilder html = new StringBuilder();

        html.append("<div style='font-family: Arial, sans-serif; padding: 20px;'>");

        html.append("<h2 style='color: #2b6cb0;'>" + titulo + "</h2>");

        if (imagenUrl != null) {
            html.append("<img src='" + imagenUrl + "' style='width:100%; max-width:600px; border-radius:8px; margin-bottom:20px;'>");
        }

        html.append("<p style='font-size: 16px; color: #444;'>");
        html.append(mensaje);
        html.append("</p>");

        html.append("</div>");

        return html.toString();
    }




}
