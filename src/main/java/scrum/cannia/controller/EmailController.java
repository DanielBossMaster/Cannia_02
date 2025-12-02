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
    public String enviarPublicidad(
            @RequestParam String titulo,
            @RequestParam String mensaje,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestParam(required = false) String correosManuales
    ) throws IOException {

        List<String> correos = propietarioRepository.obtenerCorreosDePropietarios();

        if (correosManuales != null && !correosManuales.trim().isEmpty()) {
            String[] extras = correosManuales.split("[,\\n]");
            for (String c : extras) {
                if (c.trim().contains("@")) correos.add(c.trim());
            }
        }

        byte[] imagenBytes = null;
        String nombreImagen = null;

        if (imagen != null && !imagen.isEmpty()) {
            imagenBytes = imagen.getBytes();
            nombreImagen = imagen.getOriginalFilename();
        }

        // HTML con CID
        String mensajeHtml = construirCorreoHtmlCID(titulo, mensaje);

        emailService.enviarMasivo(correos, titulo, mensajeHtml, imagenBytes, nombreImagen);

        return "redirect:/veterinario/GestionVentas";
    }



    private String construirCorreoHtmlCID(String titulo, String mensaje) {

        return "<div style='font-family: Arial; padding: 20px;'>" +
                "<h2 style='color: #2b6cb0;'>" + titulo + "</h2>" +
                "<img src='cid:bannerImagen' style='width:100%; max-width:600px; margin-bottom:20px;'>" +
                "<p style='font-size: 16px; color: #444;'>" + mensaje + "</p>" +
                "</div>";
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
